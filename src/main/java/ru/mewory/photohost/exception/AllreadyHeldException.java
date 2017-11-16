package ru.mewory.photohost.exception;

/**
 * Created by tookuk on 11/16/17.
 */
public class AllreadyHeldException extends Exception {
    public AllreadyHeldException(String changeUser) {
        super(changeUser);
    }
}
