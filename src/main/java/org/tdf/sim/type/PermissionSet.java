package org.tdf.sim.type;

import java.util.*;

/**
 * 素数乘积表示的权限集合
 */
public class PermissionSet {
    public static final String P0 = "基本权限，所有人都有";
    public static final String P1 = "权限1";
    public static final String P2 = "权限2";
    public static final String P3 = "权限3";
    public static final String P4 = "权限4";

    // 100 以内所有的素数 以及乘法单位元1
    public static final long[] PRIMES = new long[]{1, 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97};

    public static final Map<String, Long> ITEMS_MAP;

    public static final List<String> ITEMS;

    static {
        ITEMS = Collections.unmodifiableList(
                Arrays.asList(
                        P0, P1, P2, P3,
                        P4
                )
        );
        Map<String, Long> m = new HashMap<>();
        for (int i = 0; i < ITEMS.size(); i++) {
            m.put(ITEMS.get(i), PRIMES[i]);
        }
        ITEMS_MAP = Collections.unmodifiableMap(m);
    }

    private long product;

    public PermissionSet() {
        this.product = 1;
    }

    public PermissionSet(long product) {
        if(product <= 0)
            throw new RuntimeException("product in permission set must be positive");
        this.product = product;
    }

    public PermissionSet(String... arr) {
        this();
        for (String s : arr) {
            put(s);
        }
    }

    // 添加一个权限
    public void put(String auth) {
        long l = ITEMS_MAP.get(auth);
        if (product % l == 0)
            return;
        this.product = product * l;
    }

    // 删除一个权限
    public void remove(String auth) {
        long l = ITEMS_MAP.get(auth);
        if (product % l != 0)
            return;
        this.product = product / l;
    }

    // 判断是否包含某个权限
    public boolean contains(String auth) {
        return product % ITEMS_MAP.get(auth) == 0;
    }

    public long getProduct() {
        return product;
    }

    // 获取集合包含的所有权限
    public List<String> getPermissions() {
        List<String> ret = new ArrayList<>();
        for (String item : ITEMS) {
            if (contains(item)) {
                ret.add(item);
            }
        }
        return ret;
    }

    // 对两个权限集合作并集
    public PermissionSet union(PermissionSet set) {
        long ret = 1;
        for (int i = 0; i < ITEMS.size(); i++) {
            if (product % PRIMES[i] == 0 || set.product % PRIMES[i] == 0)
                ret *= PRIMES[i];
        }
        return new PermissionSet(ret);
    }
}
