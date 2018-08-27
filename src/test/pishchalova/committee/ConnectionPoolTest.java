package test.pishchalova.committee;

import com.pishchalova.committee.exception.PoolException;
import com.pishchalova.committee.pool.ConnectionPool;
import com.pishchalova.committee.pool.ProxyConnection;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ConnectionPoolTest {
    @BeforeClass
    public void init() throws PoolException{
        ConnectionPool.INSTANCE.init();
    }

    private static ConnectionPool pool = ConnectionPool.INSTANCE;
    private ProxyConnection connection;

    @Test
    public void initCP() {
        Assert.assertNotNull(pool);
    }

    @Test
    public void getConnectionFromCP() {
        connection = pool.takeConnection();
        int availableConnections = pool.getAmountAvailableConnection();
        Assert.assertEquals(7, availableConnections);
    }

    @Test
    public void releaseConnectionInCP() throws PoolException{
        pool.returnConnection(connection);
        int availableConnections = pool.getAmountAvailableConnection();
        Assert.assertEquals(8, availableConnections);
    }

    @AfterClass
    public void closeCP() throws PoolException{
        pool.closeConnectionPool();
    }
}
