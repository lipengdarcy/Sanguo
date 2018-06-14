package org.darcy.sanguo.attri;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.darcy.sanguo.en.En;

import sango.packet.PbAttribute;

public class Attributes implements Cloneable {
	public static final int ATTACK_TYPE_MAGIC = 1;
	public static final int ATTACK_TYPE_PHYSIC = 2;
	public static final int MAX_ATTR_COUNT = 100;
	public static final int INITFURY = 0;
	public static final int LEADERSHIP = 1;
	public static final int FORCE = 2;
	public static final int WISDOM = 3;
	public static final int PHYSICATK = 4;
	public static final int MAGICATK = 5;
	public static final int ATK = 6;
	public static final int MAXHP = 7;
	public static final int PHYSICATTACKDEFENSE = 8;
	public static final int MAGICATTACKDEFENSE = 9;
	public static final int CRITRATE = 10;
	public static final int CRITTIMES = 11;
	public static final int UNCRITRATE = 12;
	public static final int DODGERATE = 13;
	public static final int HITRATE = 14;
	public static final int BLOCKRATE = 15;
	public static final int UNBLOCKRATE = 16;
	public static final int FINALDAMAGE = 17;
	public static final int FINALDAMAGEDEC = 18;
	public static final int CURE = 19;
	public static final int CURED = 20;
	public static final int POISONDAMAGE = 21;
	public static final int POISONDAMAGEDEC = 22;
	public static final int FIREDAMAGE = 23;
	public static final int FIREDAMAGEDEC = 24;
	public static final int PHYDAMAGEDEC = 25;
	public static final int MAGDAMAGEDEC = 26;
	public static final int BECUREDRATE = 27;
	public static final int RATE_INITFURY = 50;
	public static final int RATE_LEADERSHIP = 51;
	public static final int RATE_FORCE = 52;
	public static final int RATE_WISDOM = 53;
	public static final int RATE_PHYSICATK = 54;
	public static final int RATE_MAGICATK = 55;
	public static final int RATE_ATK = 56;
	public static final int RATE_MAXHP = 57;
	public static final int RATE_PHYSICATTACKDEFENSE = 58;
	public static final int RATE_MAGICATTACKDEFENSE = 59;
	public static final int RATE_CRITRATE = 60;
	public static final int RATE_CRITTIMES = 61;
	public static final int RATE_UNCRITRATE = 62;
	public static final int RATE_DODGERATE = 63;
	public static final int RATE_HITRATE = 64;
	public static final int RATE_BLOCKRATE = 65;
	public static final int RATE_UNBLOCKRATE = 66;
	public static final int RATE_FINALDAMAGE = 67;
	public static final int RATE_FINALDAMAGEDEC = 68;
	public static final int RATE_CURE = 69;
	public static final int RATE_CURED = 70;
	public static final int RATE_POISONDAMAGE = 71;
	public static final int RATE_POISONDAMAGEDEC = 72;
	public static final int RATE_FIREDAMAGE = 73;
	public static final int RATE_FIREDAMAGEDEC = 74;
	public static final int RATE_PHYDAMAGEDEC = 75;
	public static final int RATE_MAGDAMAGEDEC = 76;
	public static final int RATE_BECUREDRATE = 77;
	public int[] attris = new int[100];
	private int hp;
	private int killPoints;
	private int buffFinalDamageInc;
	private int buffFinalDamageIncRate;
	public int[] baseAttris = new int[50];

	public int get(int field) {
		return this.attris[field];
	}

	public int getBase(int field) {
		if (field >= 50) {
			field -= 50;
		}
		return this.baseAttris[field];
	}

	public void set(int field, int value) {
		this.attris[field] = value;
	}

	public Object clone() {
		Attributes a = new Attributes();
		a.attris = Arrays.copyOf(this.attris, this.attris.length);
		a.hp = this.hp;
		a.killPoints = this.killPoints;
		return a;
	}

	public int getBuffFinalDamageInc() {
		return this.buffFinalDamageInc;
	}

	public void setBuffFinalDamageInc(int buffFinalDamageInc) {
		this.buffFinalDamageInc = buffFinalDamageInc;
	}

	public int getBuffFinalDamageIncRate() {
		return this.buffFinalDamageIncRate;
	}

	public int getAttriCount() {
		int r = 0;
		for (int i : this.attris) {
			if (i > 0) {
				++r;
			}
		}
		return r;
	}

	public static Attributes readObject(ObjectInputStream in) throws IOException {
		Attributes a = new Attributes();
		int count = in.readInt();
		for (int i = 0; i < count; ++i) {
			int index = in.readInt();
			int value = in.readInt();
			a.attris[index] = value;
		}
		return a;
	}

	public void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(getAttriCount());
		for (int i = 0; i < this.attris.length; ++i)
			if (this.attris[i] > 0) {
				out.writeInt(i);
				out.writeInt(this.attris[i]);
			}
	}

	public void setBuffFinalDamageIncRate(int buffFinalDamageIncRate) {
		this.buffFinalDamageIncRate = buffFinalDamageIncRate;
	}

	public int getHp() {
		return this.hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getKillPoints() {
		return this.killPoints;
	}

	public void setKillPoints(int killPoints) {
		this.killPoints = killPoints;
	}

	public void addKillPoints(int killPoints) {
		this.killPoints += killPoints;
		if (this.killPoints > 6)
			this.killPoints = 6;
	}

	public int buffFinalDamageModify(int finalDamage) {
		int rst = finalDamage + getBuffFinalDamageInc() + finalDamage * getBuffFinalDamageIncRate() / 10000;
		if (rst < 1)
			rst = 1;
		return rst;
	}

	public static void addAttr(Attributes[] attrs) {
		Attributes result = attrs[0];
		for (int i = 0; i < attrs.length; ++i)
			if ((i != 0) && (attrs[i] != null)) {
				Attributes tmp = attrs[i];
				for (int j = 0; j < 100; ++j)
					result.set(j, result.get(j) + tmp.get(j));
			}
	}

	public void rateModify() {
		int length = 50;
		for (int i = 0; i < length; ++i) {
			this.attris[i] = (int) (this.attris[i] * (10000L + this.attris[(i + length)]) / 10000L);
		}
		this.baseAttris = Arrays.copyOf(this.attris, length);
	}

	public void clear() {
		for (int j = 0; j < 100; ++j)
			set(j, 0);
	}

	public PbAttribute.Attribute genAttribute() {
		PbAttribute.Attribute.Builder builder = PbAttribute.Attribute.newBuilder();
		if (get(0) > 0)
			builder.setAngry(get(0));
		if (get(6) > 0)
			builder.setAtk(get(6));
		if (get(15) > 0)
			builder.setBlockRate(get(15));
		if (get(10) > 0)
			builder.setCritRate(get(10));
		if (get(11) > 0)
			builder.setCritTimes(get(11));
		if (get(19) > 0)
			builder.setCure(get(19));
		if (get(20) > 0)
			builder.setCured(get(20));
		if (get(13) > 0)
			builder.setDodgeRate(get(13));
		if (get(17) > 0)
			builder.setFinalDamage(get(17));
		if (get(18) > 0)
			builder.setFinalDamageDec(get(18));
		if (get(23) > 0)
			builder.setFireDamage(get(23));
		if (get(24) > 0)
			builder.setFireDamageDec(get(24));
		if (get(2) > 0)
			builder.setForce(get(2));
		if (get(14) > 0)
			builder.setHitRate(get(14));
		if (get(1) > 0)
			builder.setLeardership(get(1));
		if (get(5) > 0)
			builder.setMagicAtk(get(5));
		if (get(9) > 0)
			builder.setMagicAttackDefense(get(9));
		if (get(7) > 0)
			builder.setMaxHp(get(7));
		if (get(4) > 0)
			builder.setPhysicAtk(get(4));
		if (get(8) > 0)
			builder.setPhysicAttackDefense(get(8));
		if (get(21) > 0)
			builder.setPoisonDamage(get(21));
		if (get(22) > 0)
			builder.setPoisonDamageDec(get(22));
		if (get(16) > 0)
			builder.setUnblockRate(get(16));
		if (get(12) > 0)
			builder.setUncritRate(get(12));
		if (get(3) > 0)
			builder.setWisdom(get(3));
		if (get(25) > 0)
			builder.setPhyDamageDec(get(25));
		if (get(26) > 0)
			builder.setMagicDamageDec(get(26));
		if (get(27) > 0)
			builder.setBeCuredRate(get(27));
		if (get(50) > 0)
			builder.setRateInitfury(get(50));
		if (get(51) > 0)
			builder.setRateLeadership(get(51));
		if (get(52) > 0)
			builder.setRateForce(get(52));
		if (get(53) > 0)
			builder.setRateWisdom(get(53));
		if (get(54) > 0)
			builder.setRatePhysicatk(get(54));
		if (get(55) > 0)
			builder.setRateMagicatk(get(55));
		if (get(56) > 0)
			builder.setRateAtk(get(56));
		if (get(57) > 0)
			builder.setRateMaxhp(get(57));
		if (get(58) > 0)
			builder.setRatePhysicattackdefense(get(58));
		if (get(59) > 0)
			builder.setRateMagicattackdefense(get(59));
		if (get(60) > 0)
			builder.setRateCritRate(get(60));
		if (get(61) > 0)
			builder.setRateCritTimes(get(61));
		if (get(62) > 0)
			builder.setRateUncritRate(get(62));
		if (get(63) > 0)
			builder.setRateDodgeRate(get(63));
		if (get(64) > 0)
			builder.setRateHitRate(get(64));
		if (get(65) > 0)
			builder.setRateBlockRate(get(65));
		if (get(66) > 0)
			builder.setRateUnblockRate(get(66));
		if (get(67) > 0)
			builder.setRateFinaldamage(get(67));
		if (get(68) > 0)
			builder.setRateFinaldamagedec(get(68));
		if (get(69) > 0)
			builder.setRateCure(get(69));
		if (get(70) > 0)
			builder.setRateCured(get(70));
		if (get(71) > 0)
			builder.setRatePoisondamage(get(71));
		if (get(72) > 0)
			builder.setRatePoisondamagedec(get(72));
		if (get(73) > 0)
			builder.setRateFiredamage(get(73));
		if (get(74) > 0)
			builder.setRateFiredamagedec(get(74));
		if (get(75) > 0)
			builder.setRatePhydamagedec(get(75));
		if (get(76) > 0)
			builder.setRateMagdamagedec(get(76));
		if (get(77) > 0)
			builder.setRateBecuredRate(get(77));
		if (this.killPoints > 0) {
			builder.setKillPoints(this.killPoints);
		}
		return builder.build();
	}

	public PbAttribute.Attribute genAttributeFull() {
		PbAttribute.Attribute.Builder builder = PbAttribute.Attribute.newBuilder();
		for (int type = 0; type < 100; ++type) {
			int value = this.attris[type];
			if (value <= 0) {
				continue;
			}
			switch (type) {
			case 0:
				builder.setAngry(value);
				break;
			case 6:
				builder.setAtk(value);
				break;
			case 7:
				builder.setMaxHp(value);
				break;
			case 15:
				builder.setBlockRate(value);
				break;
			case 10:
				builder.setCritRate(value);
				break;
			case 11:
				builder.setCritTimes(value);
				break;
			case 19:
				builder.setCure(value);
				break;
			case 20:
				builder.setCured(value);
				break;
			case 13:
				builder.setDodgeRate(value);
				break;
			case 17:
				builder.setFinalDamage(value);
				break;
			case 18:
				builder.setFinalDamageDec(value);
				break;
			case 23:
				builder.setFireDamage(value);
				break;
			case 24:
				builder.setFireDamageDec(value);
				break;
			case 2:
				builder.setForce(value);
				break;
			case 14:
				builder.setHitRate(value);
				break;
			case 1:
				builder.setLeardership(value);
				break;
			case 5:
				builder.setMagicAtk(value);
				break;
			case 9:
				builder.setMagicAttackDefense(value);
				break;
			case 4:
				builder.setPhysicAtk(value);
				break;
			case 8:
				builder.setPhysicAttackDefense(value);
				break;
			case 21:
				builder.setPoisonDamage(value);
				break;
			case 22:
				builder.setPoisonDamageDec(value);
				break;
			case 16:
				builder.setUnblockRate(value);
				break;
			case 12:
				builder.setUncritRate(value);
				break;
			case 3:
				builder.setWisdom(value);
				break;
			case 25:
				builder.setPoisonDamageDec(value);
				break;
			case 26:
				builder.setUnblockRate(value);
				break;
			case 27:
				builder.setBeCuredRate(value);
				break;
			case 50:
				builder.setRateInitfury(value);
				break;
			case 51:
				builder.setRateLeadership(value);
				break;
			case 52:
				builder.setRateForce(value);
				break;
			case 53:
				builder.setRateWisdom(value);
				break;
			case 54:
				builder.setRatePhysicatk(value);
				break;
			case 55:
				builder.setRateMagicatk(value);
				break;
			case 56:
				builder.setRateAtk(value);
				break;
			case 57:
				builder.setRateMaxhp(value);
				break;
			case 58:
				builder.setRatePhysicattackdefense(value);
				break;
			case 59:
				builder.setRateMagicattackdefense(value);
				break;
			case 60:
				builder.setRateCritRate(value);
				break;
			case 61:
				builder.setRateCritTimes(value);
				break;
			case 62:
				builder.setRateUncritRate(value);
				break;
			case 63:
				builder.setRateDodgeRate(value);
				break;
			case 64:
				builder.setRateHitRate(value);
				break;
			case 65:
				builder.setRateBlockRate(value);
				break;
			case 66:
				builder.setRateUnblockRate(value);
				break;
			case 67:
				builder.setRateFinaldamage(value);
				break;
			case 68:
				builder.setRateFinaldamagedec(value);
				break;
			case 69:
				builder.setRateCure(value);
				break;
			case 70:
				builder.setRateCured(value);
				break;
			case 71:
				builder.setRatePoisondamage(value);
				break;
			case 72:
				builder.setRatePoisondamagedec(value);
				break;
			case 73:
				builder.setRateFiredamage(value);
				break;
			case 74:
				builder.setRateFiredamagedec(value);
				break;
			case 75:
				builder.setRatePhydamagedec(value);
				break;
			case 76:
				builder.setRateMagdamagedec(value);
				break;
			case 77:
				builder.setRateBecuredRate(value);
			case 28:
			case 29:
			case 30:
			case 31:
			case 32:
			case 33:
			case 34:
			case 35:
			case 36:
			case 37:
			case 38:
			case 39:
			case 40:
			case 41:
			case 42:
			case 43:
			case 44:
			case 45:
			case 46:
			case 47:
			case 48:
			case 49:
			}
		}
		return builder.build();
	}

	public static PbAttribute.Attribute genAttribute(Map<Integer, Integer> map) {
		PbAttribute.Attribute.Builder builder = PbAttribute.Attribute.newBuilder();
		Iterator itx = map.keySet().iterator();
		while (itx.hasNext()) {
			int type = ((Integer) itx.next()).intValue();
			int value = ((Integer) map.get(Integer.valueOf(type))).intValue();
			switch (type) {
			case 0:
				builder.setAngry(value);
				break;
			case 6:
				builder.setAtk(value);
				break;
			case 7:
				builder.setMaxHp(value);
				break;
			case 15:
				builder.setBlockRate(value);
				break;
			case 10:
				builder.setCritRate(value);
				break;
			case 11:
				builder.setCritTimes(value);
				break;
			case 19:
				builder.setCure(value);
				break;
			case 20:
				builder.setCured(value);
				break;
			case 13:
				builder.setDodgeRate(value);
				break;
			case 17:
				builder.setFinalDamage(value);
				break;
			case 18:
				builder.setFinalDamageDec(value);
				break;
			case 23:
				builder.setFireDamage(value);
				break;
			case 24:
				builder.setFireDamageDec(value);
				break;
			case 2:
				builder.setForce(value);
				break;
			case 14:
				builder.setHitRate(value);
				break;
			case 1:
				builder.setLeardership(value);
				break;
			case 5:
				builder.setMagicAtk(value);
				break;
			case 9:
				builder.setMagicAttackDefense(value);
				break;
			case 4:
				builder.setPhysicAtk(value);
				break;
			case 8:
				builder.setPhysicAttackDefense(value);
				break;
			case 21:
				builder.setPoisonDamage(value);
				break;
			case 22:
				builder.setPoisonDamageDec(value);
				break;
			case 16:
				builder.setUnblockRate(value);
				break;
			case 12:
				builder.setUncritRate(value);
				break;
			case 3:
				builder.setWisdom(value);
				break;
			case 25:
				builder.setPoisonDamageDec(value);
				break;
			case 26:
				builder.setUnblockRate(value);
				break;
			case 27:
				builder.setBeCuredRate(value);
				break;
			case 50:
				builder.setRateInitfury(value);
				break;
			case 51:
				builder.setRateLeadership(value);
				break;
			case 52:
				builder.setRateForce(value);
				break;
			case 53:
				builder.setRateWisdom(value);
				break;
			case 54:
				builder.setRatePhysicatk(value);
				break;
			case 55:
				builder.setRateMagicatk(value);
				break;
			case 56:
				builder.setRateAtk(value);
				break;
			case 57:
				builder.setRateMaxhp(value);
				break;
			case 58:
				builder.setRatePhysicattackdefense(value);
				break;
			case 59:
				builder.setRateMagicattackdefense(value);
				break;
			case 60:
				builder.setRateCritRate(value);
				break;
			case 61:
				builder.setRateCritTimes(value);
				break;
			case 62:
				builder.setRateUncritRate(value);
				break;
			case 63:
				builder.setRateDodgeRate(value);
				break;
			case 64:
				builder.setRateHitRate(value);
				break;
			case 65:
				builder.setRateBlockRate(value);
				break;
			case 66:
				builder.setRateUnblockRate(value);
				break;
			case 67:
				builder.setRateFinaldamage(value);
				break;
			case 68:
				builder.setRateFinaldamagedec(value);
				break;
			case 69:
				builder.setRateCure(value);
				break;
			case 70:
				builder.setRateCured(value);
				break;
			case 71:
				builder.setRatePoisondamage(value);
				break;
			case 72:
				builder.setRatePoisondamagedec(value);
				break;
			case 73:
				builder.setRateFiredamage(value);
				break;
			case 74:
				builder.setRateFiredamagedec(value);
				break;
			case 75:
				builder.setRatePhydamagedec(value);
				break;
			case 76:
				builder.setRateMagdamagedec(value);
				break;
			case 77:
				builder.setRateBecuredRate(value);
			case 28:
			case 29:
			case 30:
			case 31:
			case 32:
			case 33:
			case 34:
			case 35:
			case 36:
			case 37:
			case 38:
			case 39:
			case 40:
			case 41:
			case 42:
			case 43:
			case 44:
			case 45:
			case 46:
			case 47:
			case 48:
			case 49:
			}
		}
		return builder.build();
	}

	public static Attributes newAttributes(Map<Integer, Integer> map) {
		Iterator itx = map.keySet().iterator();
		Attributes attr = new Attributes();
		while (itx.hasNext()) {
			int type = ((Integer) itx.next()).intValue();
			int value = ((Integer) map.get(Integer.valueOf(type))).intValue();
			attr.set(type, value);
		}
		return attr;
	}

	public void addAttri(Attri a) {
		set(a.getAid(), get(a.getAid()) + a.getValue());
	}

	public void addEn(En en) {
		for (int i = 0; i < en.attriIndexs.length; ++i) {
			int index = en.attriIndexs[i];
			int value = en.attriValues[i];

			set(index, get(index) + value);
		}
	}
}
