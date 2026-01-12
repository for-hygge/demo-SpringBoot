package org.example.demospringboot.event;

public record UserChangedEvent(Type type, Long userId, String email) {
    public enum Type { CREATED, UPDATED, DELETED }
}
