package ru.mewory.mediasort.model;

import ru.mewory.mediasort.model.socnet.Post;

public class JournalElement {
    private Post post;
    private int unprocessed;
    private int processed;

    public Post getPost() {
        return post;
    }

    public JournalElement setPost(Post post) {
        this.post = post;
        return this;
    }

    public int getUnprocessed() {
        return unprocessed;
    }

    public JournalElement setUnprocessed(int unprocessed) {
        this.unprocessed = unprocessed;
        return this;
    }

    public int getProcessed() {
        return processed;
    }

    public JournalElement setProcessed(int processed) {
        this.processed = processed;
        return this;
    }
}
