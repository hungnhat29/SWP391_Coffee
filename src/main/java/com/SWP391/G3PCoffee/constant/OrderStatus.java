package com.SWP391.G3PCoffee.constant;

public enum OrderStatus {
    pending, paid, preparing, ready_to_pickup, completed, shipping, canceled;

    public OrderStatus getNextStatus(TypeOrder typeOrder) {
        return switch (this) {
            case paid -> preparing;
            case preparing -> {
                if (typeOrder.equals(TypeOrder.PICKUP)) {
                    yield ready_to_pickup;
                }
                yield shipping;
            }
            case ready_to_pickup, shipping -> completed;
            default -> this;
        };
    }
}
