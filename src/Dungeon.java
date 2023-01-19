import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Dungeon {

    private int caveXDimension; // starts bottom left -> right
    private int caveYDimension; // starts bottom left ^ up

    private AgentPos start = new AgentPos(1, 1, AgentPos.Orientation.FACING_NORTH);
    private Room wumpus;
    private Room gold;
    private Set<Room> pits = new LinkedHashSet<>();

    private Set<Room> allowedRooms;

    public Dungeon() {
        this(4,4);
    }

    public Dungeon(int caveXDimension, int caveYDimension) {
        if (caveXDimension < 1)
            throw new IllegalArgumentException("Cave must have x dimension >= 1");
        if (caveYDimension < 1)
            throw new IllegalArgumentException("Case must have y dimension >= 1");
        this.caveXDimension = caveXDimension;
        this.caveYDimension = caveYDimension;
        allowedRooms = getAllRooms();
    }

    public Dungeon(int caveXDimension, int caveYDimension, String config) {
        this(caveXDimension, caveYDimension);
        if (config.length() != 2 * caveXDimension * caveYDimension)
            throw new IllegalStateException("Wrong configuration length.");
        for (int i = 0; i < config.length(); i++) {
            char c = config.charAt(i);
            Room r = new Room(i / 2 % caveXDimension + 1, caveYDimension - i / 2 / caveXDimension);
            switch (c) {
                case 'S': start = new AgentPos(r.getX(), r.getY(), AgentPos.Orientation.FACING_NORTH); break;
                case 'W': wumpus = r; break;
                case 'G': gold = r; break;
                case 'P': pits.add(r); break;
            }
        }
    }

    public Dungeon setAllowed(Set<Room> allowedRooms) {
        this.allowedRooms.clear();
        this.allowedRooms.addAll(allowedRooms);
        return this;
    }

    public void setWumpus(Room room) {
        wumpus = room;
    }

    public void setGold(Room room) {
        gold = room;
    }

    public void setPit(Room room, boolean b) {
        if (!b)
            pits.remove(room);
        else if (!room.equals(start.getRoom()) && !room.equals(gold))
            pits.add(room);
    }

    public int getCaveXDimension() {
        return caveXDimension;
    }

    public int getCaveYDimension() {
        return caveYDimension;
    }

    public AgentPos getStart() {
        return start;
    }

    public Room getWumpus() {
        return wumpus;
    }

    public Room getGold() {
        return gold;
    }

    public boolean isPit(Room room) {
        return pits.contains(room);
    }

    public AgentPos moveForward(AgentPos position) {
        int x = position.getX();
        int y = position.getY();
        switch (position.getOrientation()) {
            case FACING_NORTH: y++; break;
            case FACING_SOUTH: y--; break;
            case FACING_EAST: x++; break;
            case FACING_WEST: x--; break;
        }
        Room room = new Room(x, y);
        start = allowedRooms.contains(room) ? new AgentPos(x, y, position.getOrientation()) : position;
        return start;
    }

    public AgentPos turnLeft(AgentPos position) {
        AgentPos.Orientation orientation = null;
        switch (position.getOrientation()) {
            case FACING_NORTH: orientation = AgentPos.Orientation.FACING_WEST; break;
            case FACING_SOUTH: orientation = AgentPos.Orientation.FACING_EAST; break;
            case FACING_EAST: orientation = AgentPos.Orientation.FACING_NORTH; break;
            case FACING_WEST: orientation = AgentPos.Orientation.FACING_SOUTH; break;
        }
        start = new AgentPos(position.getX(), position.getY(), orientation);
        return start;
    }

    public AgentPos turnRight(AgentPos position) {
        AgentPos.Orientation orientation = null;
        switch (position.getOrientation()) {
            case FACING_NORTH: orientation = AgentPos.Orientation.FACING_EAST; break;
            case FACING_SOUTH: orientation = AgentPos.Orientation.FACING_WEST; break;
            case FACING_EAST: orientation = AgentPos.Orientation.FACING_SOUTH; break;
            case FACING_WEST: orientation = AgentPos.Orientation.FACING_NORTH; break;
        }
        start = new AgentPos(position.getX(), position.getY(), orientation);
        return start;
    }

    public Set<Room> getAllRooms() {
        Set<Room> allowedRooms = new HashSet<>();
        for (int x = 1; x <= caveXDimension; x++)
            for (int y = 1; y <= caveYDimension; y++)
                allowedRooms.add(new Room(x, y));
        return allowedRooms;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int y = caveYDimension; y >= 1; y--) {
            for (int x = 1; x <= caveXDimension; x++) {
                Room r = new Room(x, y);
                String txt = "";
                if (r.equals(start.getRoom()))
                    txt += "X";
                if (r.equals(gold))
                    txt += "G";
                if (r.equals(wumpus))
                    txt += "W";
                if (isPit(r))
                    txt += "P";
                if (txt.isEmpty())
                    txt = "_";
                else if (txt.length() == 1)
                    txt += " ";
                else if ( txt.length() > 2) // cannot represent...
                    txt = txt.substring(0, 2);
                builder.append(txt);
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
