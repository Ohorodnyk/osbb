package com.softserve.osbb.service;

import com.softserve.osbb.model.Vote;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Roman on 10.07.2016.
 */

@Service
public interface VoteService {

	Vote addVote(Vote vote);

	Vote getVoteById(Integer id);

	List<Vote> getAllAvailable();

	List<Vote> getByTicketId(Integer ticketId);

	List<Vote> getByTicketIdAndAvailable(Integer ticketId);

	List<Vote> getAllVotesByDateOfCreation();

	boolean existsVote(Integer id);

	void deleteVote(Integer id);

	void deleteVote(Vote vote);

}
