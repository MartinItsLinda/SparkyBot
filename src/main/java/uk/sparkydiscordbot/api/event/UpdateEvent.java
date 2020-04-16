package uk.sparkydiscordbot.api.event;

public interface UpdateEvent<T>  {

    T getOldValue();

    T getNewValue();

}
