package ru.mewory.photohost.dao;

import ru.mewory.photohost.model.dictionaries.Dictionary;

public interface DictRepository {
    Dictionary findByName(String name);
}
