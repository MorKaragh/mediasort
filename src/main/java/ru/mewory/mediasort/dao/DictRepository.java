package ru.mewory.mediasort.dao;

import ru.mewory.mediasort.model.dictionaries.Dictionary;

public interface DictRepository {
    Dictionary findByName(String name);
}
