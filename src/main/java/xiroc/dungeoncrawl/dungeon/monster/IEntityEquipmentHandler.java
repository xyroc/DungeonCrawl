package xiroc.dungeoncrawl.dungeon.monster;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.inventory.EquipmentSlotType;

public interface IEntityEquipmentHandler<T extends LivingEntity> {

	public static final IEntityEquipmentHandler<ZombieEntity> ZOMBIE = (zombie) -> {
		zombie.setItemStackToSlot(EquipmentSlotType.MAINHAND, null);
	};

	public static final IEntityEquipmentHandler<SkeletonEntity> SKELETON = (skeleton) -> {
		
	};

	public abstract void equipEntity(T entity);

}
