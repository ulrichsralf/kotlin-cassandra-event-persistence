# kotlin-cassandra-event-persistence

Event Sourcing persistence implementation using Apache Cassandra, Spring Boot and Kotlin.

You need to have [Cassandra](http://cassandra.apache.org/download/) running to use it.


Data is saved in a journal like:

| persistenceid | partition | timestamp                            | timebucket | operation | principal | serializedtype            | serializerid | value                           |
|---------------|-----------|--------------------------------------|------------|-----------|-----------|---------------------------|--------------|---------------------------------|
|       account |         0 | 0de81580-c980-11e7-9ed0-c137d9b5e8d7 |   20171114 |    CREATE |      null | de.mc.persistence.Account |            1 |   {"id":"demo1","name":"demo1"} |
|       account |         0 | 0df16450-c980-11e7-9ed0-c137d9b5e8d7 |   20171114 |    CREATE |      null | de.mc.persistence.Account |            1 |   {"id":"demo2","name":"demo2"} |
|       account |         0 | 0dfcaef0-c980-11e7-9ed0-c137d9b5e8d7 |   20171114 |    DELETE |      null | de.mc.persistence.Account |            1 |  {"id":"demo1","name":"demo1"} |


Have a look at [this test](https://github.com/ulrichsralf/kotlin-cassandra-event-persistence/blob/master/src/test/kotlin/de/mc/persistence/JournalRepositoryTest.kt) for example usage
