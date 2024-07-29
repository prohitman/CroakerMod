package com.prohitman.croakermod.climbing.common.entity.movement;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.mojang.logging.LogUtils;
import net.minecraft.client.particle.SuspendedParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.world.level.PathNavigationRegion;
import org.jetbrains.annotations.NotNull;

public class CustomPathFinder extends PathFinder {
	private final BinaryHeap path = new BinaryHeap();
	private final Node[] neighbors = new Node[32];
	private final NodeEvaluator nodeProcessor;

	private int maxExpansions = 200;

	public static interface Heuristic {
		public float compute(Node start, Node end, boolean isTargetHeuristic);
	}

	public static final Heuristic DEFAULT_HEURISTIC = (start, end, isTargetHeuristic) -> start.distanceManhattan(end); //distanceManhattan

	private Heuristic heuristic = DEFAULT_HEURISTIC;

	public CustomPathFinder(NodeEvaluator processor, int maxExpansions) {
		super(processor, maxExpansions);
		this.nodeProcessor = processor;
		this.maxExpansions = maxExpansions;
	}

	public NodeEvaluator getNodeProcessor() {
		return this.nodeProcessor;
	}

	public CustomPathFinder setMaxExpansions(int expansions) {
		this.maxExpansions = expansions;
		return this;
	}

	public CustomPathFinder setHeuristic(Heuristic heuristic) {
		this.heuristic = heuristic;
		return this;
	}

	@Nullable
	@Override
	public Path findPath(@NotNull PathNavigationRegion region, @NotNull Mob entity, Set<BlockPos> checkpoints, float maxDistance, int checkpointRange, float maxExpansionsMultiplier) {
		this.path.clear();
		//System.out.println("In custom path finder");
		this.nodeProcessor.prepare(region, entity);

		Node pathpoint = this.nodeProcessor.getStart();
		if(pathpoint == null){
			return null;
		}	else {
			//System.out.println("Obtained starting position: " + pathpoint.asBlockPos());

			//Create a checkpoint for each block pos in the checkpoints set
			Map<Target, BlockPos> checkpointsMap = checkpoints.stream().collect(Collectors.toMap((pos) -> {
				return this.nodeProcessor.getGoal(pos.getX(), pos.getY(), pos.getZ());
			}, Function.identity()));

			Path path = this.findPath(region.getProfiler(), pathpoint, checkpointsMap, maxDistance, checkpointRange, maxExpansionsMultiplier);
			this.nodeProcessor.done();

			return path;
		}
	}

	//TODO Re-implement custom heuristics

	@Nullable
	private Path findPath(ProfilerFiller pProfiler, Node start, Map<Target, BlockPos> checkpointsMap, float maxDistance, int checkpointRange, float maxExpansionsMultiplier) {
		pProfiler.push("find_path");
		pProfiler.markForCharting(MetricCategory.PATH_FINDING);

		Set<Target> checkpoints = checkpointsMap.keySet();

		start.g = 0.0F;
		start.h = this.computeHeuristic(start, checkpoints);
		start.f = start.h;

		this.path.clear();
		this.path.insert(start);

		Set<Target> reachedCheckpoints = Sets.newHashSetWithExpectedSize(checkpoints.size());

		int expansions = 0;
		int maxExpansions = (int) (this.maxExpansions * maxExpansionsMultiplier);

		while(!this.path.isEmpty() && ++expansions < maxExpansions) {
			Node openPathPoint = this.path.pop();
			openPathPoint.closed = true;

			for(Target checkpoint : checkpoints) {
				if(openPathPoint.distanceManhattan(checkpoint) <= checkpointRange) {
					checkpoint.setReached();
					reachedCheckpoints.add(checkpoint);
				}
			}

			if(!reachedCheckpoints.isEmpty()) {
				//System.out.println("Breaking...");
				break;
			}

			if(openPathPoint.distanceTo(start) < maxDistance) {
				int numOptions = this.nodeProcessor.getNeighbors(this.neighbors, openPathPoint);
				//System.out.println("Reached here!!!!!!!" + numOptions + Arrays.toString(this.neighbors));

				for(int i = 0; i < numOptions; ++i) {
					Node successorPathPoint = this.neighbors[i];

					float costHeuristic = openPathPoint.distanceTo(successorPathPoint); //TODO Replace with cost heuristic

					//field_222861_j corresponds to the total path cost of the evaluation function
					successorPathPoint.walkedDistance = openPathPoint.walkedDistance + costHeuristic;

					float totalSuccessorPathCost = openPathPoint.g + costHeuristic + successorPathPoint.costMalus;

					if(successorPathPoint.walkedDistance < maxDistance && (!successorPathPoint.inOpenSet() || totalSuccessorPathCost < successorPathPoint.g)) {
						successorPathPoint.cameFrom = openPathPoint;
						successorPathPoint.g = totalSuccessorPathCost;

						//distanceToNext corresponds to the heuristic part of the evaluation function
						successorPathPoint.h = this.computeHeuristic(successorPathPoint, checkpoints) * 1.0f; //TODO Vanilla's 1.5 multiplier is too greedy :( Move to custom heuristic stuff

						if(successorPathPoint.inOpenSet()) {
							this.path.changeCost(successorPathPoint, successorPathPoint.g + successorPathPoint.h);
						} else {
							//distanceToTarget corresponds to the evaluation function, i.e. total path cost + heuristic
							successorPathPoint.f = successorPathPoint.g + successorPathPoint.h;
							//System.out.println("Inserted new point: " + successorPathPoint.asBlockPos());
							this.path.insert(successorPathPoint);
						}
					}
				}
			}
		}

		Optional<Path> optional = !reachedCheckpoints.isEmpty() ? reachedCheckpoints.stream().map((checkpoint) -> {
			return this.createPath(checkpoint.getBestNode(), checkpointsMap.get(checkpoint), true);
		}).min(Comparator.comparingInt(Path::getNodeCount)) : checkpoints.stream().map((checkpoint) -> {
			//System.out.println("Checkpoint here: " + checkpoint.asBlockPos());
			return this.createPath(checkpoint.getBestNode(), checkpointsMap.get(checkpoint), false);
		}).min(Comparator.comparingDouble(Path::getDistToTarget).thenComparingInt(Path::getNodeCount));

/*		Optional<Path> path;

		if(!reachedCheckpoints.isEmpty()) {
			System.out.println("In here?");
			//Use shortest path towards next reached checkpoint
			path = reachedCheckpoints.stream().map((checkpoint) -> {
				return this.createPath(checkpoint.getBestNode(), checkpointsMap.get(checkpoint), true);
			}).min(Comparator.comparingInt(Path::getNodeCount));
		} else {
			System.out.println("Or here?");

			//Use lowest cost path towards any checkpoint
			path = checkpoints.stream().map((checkpoint) -> {
				return this.createPath(checkpoint.getBestNode(), checkpointsMap.get(checkpoint), false);
			}).min(Comparator.comparingDouble(Path::getDistToTarget *//*TODO Replace calculation with cost heuristic*//*).thenComparingInt(Path::getNodeCount));
		}*/
		pProfiler.pop();
		//System.out.println("Created path :: " + (optional.map(value -> (value.getNodeCount() + " " + value.getTarget())).orElse("")));
		return !optional.isPresent() ? null : optional.get();
	}

	private float computeHeuristic(Node pathPoint, Set<Target> checkpoints) {
		float minDst = Float.MAX_VALUE;

		for(Target checkpoint : checkpoints) {
			float dst = pathPoint.distanceTo(checkpoint); //TODO Replace with target heuristic
			checkpoint.updateBest(dst, pathPoint);
			minDst = Math.min(dst, minDst);
		}

		return minDst;
	}

	protected Path createPath(Node start, BlockPos target, boolean isTargetReached) {
		List<Node> points = Lists.newArrayList();

		Node currentPathPoint = start;
		points.add(0, start);

		while(currentPathPoint.cameFrom != null) {
			currentPathPoint = currentPathPoint.cameFrom;
			points.add(0, currentPathPoint);
		}

		return new Path(points, target, isTargetReached);
	}
}
