package me.raindance.riot.checks.reach;

import com.podcrash.api.location.BoundingBox;
import com.podcrash.api.location.Coordinate;
import me.raindance.riot.checks.Check;
import me.raindance.riot.events.ClientHitEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import static me.raindance.riot.util.DistanceUtil.distanceBox;

/**
 * ReachB for boundingbox
 */
public class ReachB extends Check {
    @EventHandler
    public void hit(ClientHitEvent e) {
        Player player = e.getPlayer();
        Entity entity = e.getEntity();
        Coordinate currLocation = Coordinate.fromLocation(player.getLocation());
        Coordinate headPoint = currLocation.add(0, player.getEyeHeight(), 0);

        double minimumDistanceRequired = distanceBox(headPoint, new BoundingBox(entity));
        if (minimumDistanceRequired <= 3.3D)
            return;
        alert(player.getName() + " might be clienting");
    }
}
