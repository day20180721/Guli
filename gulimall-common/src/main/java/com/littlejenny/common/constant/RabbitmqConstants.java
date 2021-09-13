package com.littlejenny.common.constant;

public class RabbitmqConstants {
    public static String HANDLESTOCK_REAL_QUEUE = "stock.release.stock.queue";
    public static String HANDLESTOCK_REAL_QUEUE_KEY = "stock.release.#";
    public static String HANDLESTOCK_DELAY_QUEUE = "stock.delay.queue";
    public static String HANDLESTOCK_DELAY_QUEUE_KEY = "stock.locked";
    public static String HANDLESTOCK_EXCHANGE = "stock-event-exchange";

    public static String HANDLEORDER_REAL_QUEUE = "order.release.order.queue";
    public static String HANDLEORDER_REAL_QUEUE_KEY = "order.release.order";
    public static String HANDLEORDER_DELAY_QUEUE = "order.delay.queue";
    public static String HANDLEORDER_DELAY_QUEUE_KEY = "order.locked";
    public static String HANDLEORDER_EXCHANGE = "order-event-exchange";

    public static String HANDLEORDER_TOSTOCK_QUEUE_KEY = "order.release.other.#";


    public static String HANDLESECORDER_QUEUE = "order.seckill.order.queue";
    public static String HANDLESECORDER_QUEUE_KEY = "order.seckill.order";

}
