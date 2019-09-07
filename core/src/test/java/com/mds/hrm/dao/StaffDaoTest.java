package com.mds.hrm.dao;

import com.mds.common.dao.BaseDaoTestCase;
import com.mds.hrm.model.Staff;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class StaffDaoTest extends BaseDaoTestCase {
    @Autowired
    private StaffDao staffDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveStaff() {
        Staff staff = new Staff();

        // enter all required fields
        staff.setChineseName("MfLhHtCbJnYfLtKeWcVoMlHiAmBuKqKdWuQdKzDzJbBvQjLdRm");
        staff.setGender("L");
        staff.setGivenName("ByQeDmOrKhLaLtGuRqDlUnYaPpVjPmXaMgBfAaGbSmYvBoWjAbDbLkNvSlToFuOtDxOiHfLjPbFrWnGoXrIrKeUgLsViOzHiWdSm");
        staff.setJoinDate(new java.util.Date());
        staff.setMarital("ZfHsXfVjJqOlYtKoGjEsZlAlDyQpFbFpYhNlQbQyNzAlJuIzOdBoJyUrKdXrRxUlLoUfRxUoDhMwZgZtWoAnSjBuDpJeDuBsSbIaNsBjDxPyHzOyDjYlSaXnDqZqUlWsAwWnMpMzSrWyYrQbFvRfJlWdDkFqDiQuTxVtAxZtWnEpIlBkSvScYbKuUuBlHcJmAkOsHiQrCtOqQcOnBgJcIzLuIuMpNfInEeGvEmUiPdZxYeEzVuUrHbNiXyXhZqO");
        staff.setStaffNo("LxRoYcGwEiVvKnMhRjQuVjDuGrXeCvEhGjRbErAxAqTdTtUoXnUkGuRlFvNnHoRpFqYhRdUtSzEkSuYmBvJaUvMqDbZzVlSnGnIu");
        staff.setStatus("S");
        staff.setSurname("GmMiXrQjZaZiQfQdBuDtFiYyImEgGzSyLtQhKxJzBuCaBxKaGqQmAdAeUuLuYxCgCuLwKgGpYmJnIwJaJgFrKkDbJoOcYrGiGrMz");

        log.debug("adding staff...");
        staff = staffDao.save(staff);

        staff = staffDao.get(staff.getId());

        assertNotNull(staff.getId());

        log.debug("removing staff...");

        staffDao.remove(staff.getId());

        // should throw DataAccessException 
        staffDao.get(staff.getId());
    }
}