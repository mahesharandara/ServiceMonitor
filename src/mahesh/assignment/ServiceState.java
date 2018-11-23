package mahesh.assignment;

/**
 *  state RUNNING or NOT_RUNNING
 */

public class ServiceState {
    public enum State {
        UNKNOWN,
        SERVICE_UP,
        SERVICE_DOWN;
    }

    private long stateTimestamp;
    private State state = State.UNKNOWN;

    /**
     * @param timestamp of the event
     * @param afterNSeconds grace period before temporary state change is considered permanent
     * @return true if server was OFF before, and now was switched ON
     */
    public boolean markAsRunning(long timestamp, int afterNSeconds) {
        switch (state) {
            case UNKNOWN:
                state = State.SERVICE_UP;
                stateTimestamp = System.currentTimeMillis();
                return false;

            case SERVICE_DOWN:
                // this is _possible_ server UP event
                if (stateTimestamp + afterNSeconds * 1000 <= timestamp) {
                    // this is server UP event for SURE
                    state = State.SERVICE_UP;
                    stateTimestamp = timestamp;
                    return true;
                }
                return false;

            case SERVICE_UP:
                // falls thru
            default:
                return false;
        }
    }

    /**
     * @param timestamp of the event
     * @param afterNSeconds grace period before temporary state change is considered permanent
     * @return true if server was ON before, and now was switched OFF
     */
    public boolean markAsNotRunning(long timestamp, int afterNSeconds) {
        switch (state) {
            case UNKNOWN:
                state = State.SERVICE_DOWN;
                stateTimestamp = System.currentTimeMillis();
                return false;

            case SERVICE_UP:
                // this is _possible_ server DOWN event
                if (stateTimestamp + afterNSeconds * 1000 <= timestamp) {
                    // this is server DOWN event for SURE
                    state = State.SERVICE_DOWN;
                    stateTimestamp = timestamp;
                    return true;
                }
                return false;

            case SERVICE_DOWN:
                // falls thru
            default:
                return false;
        }
    }
}
