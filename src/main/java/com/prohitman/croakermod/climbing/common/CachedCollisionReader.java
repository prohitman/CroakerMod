package com.prohitman.croakermod.climbing.common;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CachedCollisionReader implements CollisionGetter {
	private final CollisionGetter collisionReader;
	private final BlockGetter[] blockReaderCache;
	private final int minChunkX, minChunkZ, width;

	public CachedCollisionReader(CollisionGetter collisionReader, AABB aabb) {
		this.collisionReader = collisionReader;

		this.minChunkX = ((Mth.floor(aabb.minX - 1.0E-7D) - 1) >> 4);
		int maxChunkX = ((Mth.floor(aabb.maxX + 1.0E-7D) + 1) >> 4);
		this.minChunkZ = ((Mth.floor(aabb.minZ - 1.0E-7D) - 1) >> 4);
		int maxChunkZ = ((Mth.floor(aabb.maxZ + 1.0E-7D) + 1) >> 4);

		this.width = maxChunkX - this.minChunkX + 1;
		int depth = maxChunkZ - this.minChunkZ + 1;

		BlockGetter[] blockReaderCache = new BlockGetter[width * depth];

		for(int cx = minChunkX; cx <= maxChunkX; cx++) {
			for(int cz = minChunkZ; cz <= maxChunkZ; cz++) {
				blockReaderCache[(cx - minChunkX) + (cz - minChunkZ) * width] = collisionReader.getChunkForCollisions(cx, cz);
			}
		}

		this.blockReaderCache = blockReaderCache;
	}

	@Override
	public WorldBorder getWorldBorder() {
		return this.collisionReader.getWorldBorder();
	}

	@Nullable
	@Override
	public BlockGetter getChunkForCollisions(int pChunkX, int pChunkZ) {
		return this.blockReaderCache[(pChunkX - minChunkX) + (pChunkZ - minChunkZ) * width];
	}

	@Override
	public List<VoxelShape> getEntityCollisions(@Nullable Entity pEntity, AABB pCollisionBox) {
		return this.collisionReader.getEntityCollisions(pEntity, pCollisionBox);
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pPos) {
		return this.collisionReader.getBlockEntity(pPos);
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return this.collisionReader.getBlockState(pos);
	}

	@Override
	public FluidState getFluidState(BlockPos pPos) {
		return this.collisionReader.getFluidState(pPos);
	}

	@Override
	public int getHeight() {
		return collisionReader.getHeight();
	}

	@Override
	public int getMinBuildHeight() {
		return collisionReader.getMinBuildHeight();
	}
}
