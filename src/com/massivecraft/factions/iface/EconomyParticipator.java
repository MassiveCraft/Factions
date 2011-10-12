package com.massivecraft.factions.iface;

import com.nijikokun.register.payment.Method.MethodAccount;

public interface EconomyParticipator extends RelationParticipator
{
	public MethodAccount getAccount();
	
	public void msg(String str, Object... args);
}