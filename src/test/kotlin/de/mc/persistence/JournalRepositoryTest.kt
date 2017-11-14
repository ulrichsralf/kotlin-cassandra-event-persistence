package de.mc.persistence

import de.mc.persistence.journal.core.JournalMapView
import de.mc.persistence.journal.core.JournalRepository
import junit.framework.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

/**
 * @author Ralf Ulrich
 * 28.02.17
 */

data class Account(val id: String, val name: String)

@Service
open class AccountRepository : JournalRepository<Account>("account", Account::class.java)

@Component
open class AccountView : JournalMapView<String, Account>(Account::id)


@SpringBootTest()
@RunWith(SpringJUnit4ClassRunner::class)
open class JournalRepositoryTest {

    @Autowired
    lateinit var repo: AccountRepository
    @Autowired
    lateinit var view: AccountView

    // testdata is located in resources/data and loaded on startup
    @Test
    fun testLoadInitialData() {
        val result1 = view.getById("demo1")
        val result2 = view.getById("demo2")
        val result3 = view.getById("demo3")
        assertNotNull(result1)
        assertNotNull(result2)
        assertNull(result3)
    }

    @Test
    fun testInsertRepoQueryView() {
        for (i in 1..10) {
            repo.create(Account("testid${i}", "name"))
        }
        val result = view.query{ it.id.startsWith("testid")}
        assertEquals(10, result.size)
    }

    @Test
    fun testDelete() {
        for (i in 1..10) {
            repo.create(Account("testid${i}", "name"))
        }
        val result1 = view.query{ it.id.startsWith("testid")}
        assertEquals(10, result1.size)
        for (i in 1..10) {
            repo.delete(Account("testid${i}", "name"))
        }
        val result2 = view.query{ it.id.startsWith("testid")}
        assertEquals(0, result2.size)
    }

}
