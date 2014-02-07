package distsys;

/**
 * Created by thb on 07.02.14.
 */
import java.security.*;
import java.util.*;

/**
 * This is a SecurityManager that grants all kinds of permissions, but
 * logs and outputs any non-standard permissions granted.
 */
public class CustomSecurity extends SecurityManager {
    private Hashtable grantedPermissions;

    public CustomSecurity() {
        grantedPermissions = new Hashtable();
    }

    /**
     * Override checkPermission to grant all kind of permissions:
     */
    public void checkPermission(Permission perm) {
        try {
            super.checkPermission(perm);
        } catch(AccessControlException ace) {
            if(grantedPermissions.get(perm) == null) {
                //System.out.println("LiberalSecurityManager granted permission: "+perm);
                grantedPermissions.put(perm, perm);
            }
        }
    }
}
