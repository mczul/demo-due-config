package de.mczul.configuration.common;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntryRepository extends JpaRepository<Entry, Integer> {

    List<Entry> findByKey(String key);

}
