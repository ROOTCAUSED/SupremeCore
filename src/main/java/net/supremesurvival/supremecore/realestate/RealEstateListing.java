package net.supremesurvival.supremecore.realestate;

public record RealEstateListing(
        int id,
        String townName,
        String worldName,
        int centerX,
        int centerZ,
        double price
) {
}
