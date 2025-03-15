package com.SWP391.G3PCoffee.constant;

public enum OrderStatus {
    pending, paid, preparing, ready_to_pickup, ready_for_delivery, completed, shipping, canceled;

    public OrderStatus getNextStatus(TypeOrder typeOrder, String payment) {
        return switch (this) {
            case pending -> payment.equalsIgnoreCase("VNPAY") ? paid : preparing;
            case paid -> payment.equalsIgnoreCase("COD") ? completed : preparing;
            case preparing -> {
                if (typeOrder.equals(TypeOrder.PICKUP)) {
                    yield ready_to_pickup;
                }else{
                    yield ready_for_delivery;
                }
            }
            case ready_for_delivery -> shipping;
            case ready_to_pickup -> payment.equalsIgnoreCase("COD") ? paid : completed;
            case shipping -> payment.equalsIgnoreCase("VNPAY") ? completed : paid;
            default -> this;
        };
    }
}

