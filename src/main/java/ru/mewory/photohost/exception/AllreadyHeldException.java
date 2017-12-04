package ru.mewory.photohost.exception;

import ru.mewory.photohost.model.socnet.CommentStatus;

/**
 * Created by tookuk on 11/16/17.
 */
public class AllreadyHeldException extends Exception {
    private CommentStatus status;
    public AllreadyHeldException(String changeUser, CommentStatus status) {
        super(changeUser);
        this.status = status;
    }

    public CommentStatus getStatus() {
        return status;
    }
}
