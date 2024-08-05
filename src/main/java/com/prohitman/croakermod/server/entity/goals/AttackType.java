package com.prohitman.croakermod.server.entity.goals;

public enum AttackType {
    TONGUE(26, 25, 5, 5),
    ARM(22, 16, 2.5D, 2.5D),
    ARM_SECOND(22, 16, 2.5D, 2.5D),
    ARM_BOTH(22, 16, 2.5D, 5D),
    NONE(0, 0, 0, 0);

    public final int attackDuration;
    public final int attackTick;
    public final double attackReach;
    public final double attackDamage;

    AttackType(int attackDuration, int attackTick, double attackReach, double attackDamage){
        this.attackDuration = attackDuration;
        this.attackTick = attackTick;
        this.attackReach = attackReach;
        this.attackDamage = attackDamage;
    }

    public double getAttackDamage() {
        return attackDamage;
    }

    public int getAttackDuration() {
        return attackDuration;
    }

    public double getAttackReach() {
        return attackReach;
    }

    public int getAttackTick() {
        return attackTick;
    }

    public static AttackType getTypeWithID(int id){
        for(AttackType value : AttackType.values()){
            if(value.ordinal() == id){
                return value;
            }
        }

        return AttackType.NONE;
    }
}
