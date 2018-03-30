package ua.com.pandasushi.database.common;

        import java.io.Serializable;
        import java.util.Date;

public class CourierCommand implements Serializable {
    private Integer courierId;
    private CourierOrder order;
    private Commands command;

    public Integer getCourierId() {
        return courierId;
    }

    public void setCourierId(Integer courierId) {
        this.courierId = courierId;
    }

    public Commands getCommand() {
        return command;
    }

    public void setCommand(Commands command) {
        this.command = command;
    }

    public CourierOrder getOrder() {
        return order;
    }

    public void setOrder(CourierOrder order) {
        this.order = order;
    }
}
