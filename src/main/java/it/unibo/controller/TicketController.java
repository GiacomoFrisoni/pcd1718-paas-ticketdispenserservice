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
@RequestMapping(value="/app")
public class TicketController {
	
	@Autowired TicketConfig config;
    @Autowired RedisTemplate<String, Long> redis;

    @RequestMapping(value="/ticket/{roomId}", method = RequestMethod.GET)
    public Long getTicket(@PathVariable("roomId") final String roomId, final javax.servlet.http.HttpServletRequest req) {
        
    	// Increments atomically the ticket value for the requested room
        final Long n = (Long) redis.execute(new SessionCallback<List<Object>>() {
        	@SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public List<Object> execute(final RedisOperations ops) throws DataAccessException {
                ops.multi();
                ops.opsForValue().increment(roomId, 1);
                return ops.exec();
            }
        }).stream().reduce((a,b) -> b).get();

        // Random delay in order to simulate concurrency
    	try {
			Thread.sleep((long) (Math.random() * 1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
        return n;
        
    }
    
    @RequestMapping(value="/ticket/{roomId}/count", method = RequestMethod.GET)
    public Long countTicketsInRoom(@PathVariable("roomId") final String roomId, final javax.servlet.http.HttpServletRequest req) {
        
    	if (redis.hasKey(roomId)) {
    		final Long n = (Long) redis.execute(new SessionCallback<List<Object>>() {
            	@SuppressWarnings({ "rawtypes", "unchecked" })
                @Override
                public List<Object> execute(final RedisOperations ops) throws DataAccessException {
                    ops.multi();
                    ops.opsForValue().increment(roomId, 0);
                    return ops.exec();
                }
            }).stream().reduce((a,b) -> b).get();
    		return n;
    	} else {
    		// If there is no ticket value associated to the room, the counter is zero (new room)
    		return 0L;
    	}
		
    }
    
    @RequestMapping(value="/ticket/{roomId}/reset", method = RequestMethod.POST)
    public void resetTicket(@PathVariable("roomId") final String roomId, final javax.servlet.http.HttpServletRequest req) {
        
    	// Resets atomically the ticket value for the requested room
    	redis.execute(new SessionCallback<List<Object>>() {
            @SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
            public List<Object> execute(final RedisOperations ops) throws DataAccessException {
                ops.multi();
                ops.delete(roomId);
                return ops.exec();
            }
        });
        
    }
    
    @RequestMapping(value="/", method = RequestMethod.GET)
    public String msg(){
        return "TicketDispencerService v." + config.getAppVersion();
    }
	
}
