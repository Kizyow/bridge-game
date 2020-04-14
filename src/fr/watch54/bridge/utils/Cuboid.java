package fr.watch54.bridge.utils;


import org.bukkit.Location;

public class Cuboid {

    public static boolean isInCube(Location loc, Location corner1, Location corner2){

        int xMin = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int yMin = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int zMin = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int xMax = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int yMax = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int zMax = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
        if((loc.getX() <= xMax) && (loc.getX() >= xMin)) {
            if ((loc.getY() <= yMax) && (loc.getY() >= yMin)) {
                return (loc.getZ() <= zMax) && (loc.getZ() >= zMin);

            }
            return false;

        }
        return false;

    }

    public static boolean isInCircle(Location location, Location center, int radius){
        return location.distanceSquared(center) < radius*radius;

    }

}
