package org.ishausa.marketing.guests.model;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by Prasanna Venkat on 3/24/2017.
 */
public class RoleTest {
    @Test
    public void compareRoleHierarchy() {
        assertTrue(Role.ADMIN.compareTo(Role.EVENT_ADMIN) > 0);
        assertTrue(Role.ADMIN.compareTo(Role.REGULAR) > 0);
        assertTrue(Role.EVENT_ADMIN.compareTo(Role.REGULAR) > 0);
    }
}
