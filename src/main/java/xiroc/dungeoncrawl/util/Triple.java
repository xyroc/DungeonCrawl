package xiroc.dungeoncrawl.util;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

public class Triple<L, M, R> {

	public L l;
	public M m;
	public R r;

	public Triple(L l, M m, R r) {
		this.l = l;
		this.m = m;
		this.r = r;
	}

	public L getL() {
		return l;
	}

	public M getM() {
		return m;
	}

	public R getR() {
		return r;
	}

}
