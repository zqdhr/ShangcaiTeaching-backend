package org.tdf.sim.type;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class PermissionSetTest {

    @Test
    public void test0(){
        PermissionSet s = new PermissionSet(PermissionSet.P1, PermissionSet.P2);
        assertTrue("基本权限, 默认就有", s.contains(PermissionSet.P0));
        assertTrue("添加的权限", s.contains(PermissionSet.P1));
        assertFalse("没有a3权限", s.contains(PermissionSet.P3));
        assertFalse("没有a4权限", s.contains(PermissionSet.P4));
        s.put(PermissionSet.P4);
        assertTrue("有a4权限", s.contains(PermissionSet.P4));
        s.remove(PermissionSet.P4);
        assertFalse("删除了a4权限", s.contains(PermissionSet.P4));

    }

    @Test
    public void test1(){
        PermissionSet s = new PermissionSet(PermissionSet.P1, PermissionSet.P2);
        PermissionSet s2 = new PermissionSet(PermissionSet.P4);
        PermissionSet s3 = s.union(s2);
        assertTrue("添加的权限", s3.contains(PermissionSet.P1));
        assertTrue("有a2权限", s3.contains(PermissionSet.P2));
        assertTrue("有a4权限", s3.contains(PermissionSet.P4));
        assertFalse("没有a3权限", s3.contains(PermissionSet.P3));
    }
}
