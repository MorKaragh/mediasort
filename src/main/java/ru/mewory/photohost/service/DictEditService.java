package ru.mewory.photohost.service;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mewory.photohost.dao.*;
import ru.mewory.photohost.model.Record;
import ru.mewory.photohost.model.dictionaries.DictsEditHistory;
import ru.mewory.photohost.model.dictionaries.DictsEditTransaction;
import ru.mewory.photohost.model.dictionaries.Location;
import ru.mewory.photohost.model.dictionaries.Theme;

import java.util.List;

@Service
public class DictEditService {

    @Autowired
    private DictEditRepository dictEditRepository;

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Transactional
    public void changeDictionary(String oldName, String newName, Class dictClass) {
        DictsEditTransaction transaction = new DictsEditTransaction()
                .setDictClassName(dictClass.getName())
                .setNewDictName(newName)
                .setPrevDictName(oldName);

        if (dictClass.equals(Location.class)) {
            changeLocation(oldName, newName, transaction);
        } else {
            changeTheme(oldName, newName, transaction);
        }
    }


    private void changeTheme(String oldName, String newName, DictsEditTransaction transaction) {
        Theme oldTheme = themeRepository.findByName(oldName);
        List<Record> records = recordRepository.findByTheme(oldName);
        if (CollectionUtils.isNotEmpty(records) && oldTheme != null) {

            if (themeRepository.findByName(newName) == null) {
                Theme newTheme = new Theme();
                newTheme.setName(newName);
                themeRepository.save(newTheme);
                transaction.setWasCreatedNewEntry(true);
            }

            themeRepository.delete(oldTheme);

            for (Record r : records) {
                r.setTheme(newName);
                transaction.getHistory().add(new DictsEditHistory()
                        .setComment(r)
                        .setTransaction(transaction));
                recordRepository.save(r);
            }

            dictEditRepository.save(transaction);
        }
    }


    private void changeLocation(String oldName, String newName, DictsEditTransaction transaction) {
        Location oldLocation = locationRepository.findByName(oldName);
        List<Record> records = recordRepository.findByLocation(oldName);
        if (CollectionUtils.isNotEmpty(records) && oldLocation != null) {

            if (locationRepository.findByName(newName) == null) {
                Location newLocation = new Location();
                newLocation.setName(newName);
                locationRepository.save(newLocation);
                transaction.setWasCreatedNewEntry(true);
            }

            locationRepository.delete(oldLocation);

            for (Record r : records) {
                r.setLocation(newName);
                transaction.getHistory().add(new DictsEditHistory()
                        .setComment(r)
                        .setTransaction(transaction));
                recordRepository.save(r);
            }

            dictEditRepository.save(transaction);
        }
    }

    @Transactional
    public void rollbackTransaction() {
        Long maxId = dictEditRepository.getMaxId();
        if (maxId == null) {
            return;
        }
        DictsEditTransaction transaction = dictEditRepository.findById(maxId).get();

        if (Location.class.getName().equals(transaction.getDictClassName())) {
            for (DictsEditHistory history : transaction.getHistory()) {
                history.getComment().setLocation(transaction.getPrevDictName());
                recordRepository.save(history.getComment());
            }
            if (transaction.getWasCreatedNewEntry()) {
                locationRepository.delete(locationRepository.findByName(transaction.getNewDictName()));
            }
            if (locationRepository.findByName(transaction.getPrevDictName()) == null) {
                locationRepository.save(new Location().setName(transaction.getPrevDictName()));
            }
        } else {
            for (DictsEditHistory history : transaction.getHistory()) {
                history.getComment().setTheme(transaction.getPrevDictName());
                recordRepository.save(history.getComment());
            }
            if (transaction.getWasCreatedNewEntry()) {
                themeRepository.delete(themeRepository.findByName(transaction.getNewDictName()));
            }
            if (themeRepository.findByName(transaction.getPrevDictName()) == null) {
                themeRepository.save(new Theme().setName(transaction.getPrevDictName()));
            }
        }

        dictEditRepository.delete(transaction);
    }

}
