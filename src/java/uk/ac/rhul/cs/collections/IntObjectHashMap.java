//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package uk.ac.rhul.cs.collections;

import com.sosnoski.util.hashmap.PrimitiveKeyBase;

public class IntObjectHashMap extends PrimitiveKeyBase {
    protected int[] m_keyTable;
    protected Object[] m_valueTable;

    public IntObjectHashMap(int var1, double var2) {
        super(var1, var2, Integer.TYPE, Object.class);
    }

    public IntObjectHashMap(int var1) {
        this(var1, 0.3D);
    }

    public IntObjectHashMap() {
        this(0, 0.3D);
    }

    public IntObjectHashMap(IntObjectHashMap var1) {
        super(var1);
    }

    protected final Object getKeyArray() {
        return this.m_keyTable;
    }

    protected final void setKeyArray(Object var1) {
        this.m_keyTable = (int[])var1;
    }

    protected final Object getValueArray() {
        return this.m_valueTable;
    }

    protected final void setValueArray(Object var1) {
        this.m_valueTable = (Object[])var1;
    }

    protected final boolean reinsert(int var1) {
        super.m_flagTable[var1] = false;
        return this.assignSlot(this.m_keyTable[var1], this.m_valueTable[var1]) != var1;
    }

    protected void restructure(boolean[] var1, Object var2, Object var3) {
        int[] var4 = (int[])var2;
        Object[] var5 = (Object[])var3;

        for(int var6 = 0; var6 < var1.length; ++var6) {
            if(var1[var6]) {
                this.assignSlot(var4[var6], var5[var6]);
            }
        }

    }

    protected final int computeSlot(int var1) {
        return (var1 * 517 & 2147483647) % super.m_flagTable.length;
    }

    protected int assignSlot(int var1, Object var2) {
        int var3 = this.freeSlot(this.computeSlot(var1));
        super.m_flagTable[var3] = true;
        this.m_keyTable[var3] = var1;
        this.m_valueTable[var3] = var2;
        return var3;
    }

    public Object add(int var1, Object var2) {
        this.ensureCapacity(super.m_entryCount + 1);
        int var3 = this.internalFind(var1);
        if(var3 >= 0) {
            Object var4 = this.m_valueTable[var3];
            this.m_valueTable[var3] = var2;
            return var4;
        } else {
            ++super.m_entryCount;
            var3 = -var3 - 1;
            super.m_flagTable[var3] = true;
            this.m_keyTable[var3] = var1;
            this.m_valueTable[var3] = var2;
            return null;
        }
    }

    protected final int internalFind(int var1) {
        int var2;
        for(var2 = this.computeSlot(var1); super.m_flagTable[var2]; var2 = this.stepSlot(var2)) {
            if(var1 == this.m_keyTable[var2]) {
                return var2;
            }
        }

        return -var2 - 1;
    }

    public final boolean containsKey(int var1) {
        return this.internalFind(var1) >= 0;
    }

    public final Object get(int var1) {
        int var2 = this.internalFind(var1);
        return var2 >= 0 ? this.m_valueTable[var2] : null;
    }

    public Object remove(int var1) {
        int var2 = this.internalFind(var1);
        if(var2 < 0) {
            return null;
        } else {
            Object var3 = this.m_valueTable[var2];
            super.m_flagTable[var2] = false;
            --super.m_entryCount;

            while(super.m_flagTable[var2 = this.stepSlot(var2)]) {
                this.reinsert(var2);
            }

            return var3;
        }
    }

    public Object clone() {
        return new IntObjectHashMap(this);
    }
}
