package it.unibo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import it.unibo.config.TicketConfig;

@RestController
@RequestMapping(value="/v{ver}/app")
public class TicketController {
	
	@Autowired TicketConfig config;
    @Autowired RedisTemplate<String, Long> redis;

    @RequestMapping(value="/ticket/{roomId}", method = RequestMethod.GET)
    public Long ticket(@PathVariable("ver") final int ver, @PathVariable("roomId") final String roomId, final javax.servlet.http.HttpServletRequest req) {
        
    	// Gets the version number (if not specified, it uses the configuration one as default)
    	final int v = (config != null) ? config.getAppVersion() : ver;
    	
    	// Increments atomically the ticket value for the requested room
    	@SuppressWarnings({ "rawtypes", "unchecked" })
        final Long n = (Long) redis.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations ops) throws DataAccessException {
                ops.multi();
                ops.opsForValue().increment(roomId, 1);
                return ops.exec();
            }
        }).stream().reduce((a,b) -> b).get();

        return n;
        
    }
	
}
