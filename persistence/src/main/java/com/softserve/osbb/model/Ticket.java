package com.softserve.osbb.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.softserve.osbb.model.enums.TicketState;

/**
 * Created by cavayman on 05.07.2016.
 */

@Entity
@Table(name = "ticket")
public class Ticket implements Serializable  {
    
    private static final long serialVersionUID = 1L;
    private Integer ticketId;
    private String name;
    private String description;
    private TicketState state;
    private Timestamp stateTime;
    private Timestamp time;
    private Timestamp discussed;
    private Timestamp deadline;
    private User user;
    private User assigned;
    private Collection<Message> messages = new ArrayList<>();
    private Collection<Attachment> attachments=new ArrayList<>();
    private List<Vote> vote;

    public Ticket() {
    }

	public Ticket(String name, String description) {
        this.name = name;
        this.description = description;
        state = TicketState.NEW;
    }

    public Ticket(String name, String description, User user) {
        this.name = name;
        this.description = description;
        this.user = user;
        state = TicketState.NEW;

    }

    @Id
    @Column(name = "ticket_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getTicketId() {
        return ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "time")
    public Timestamp getTime() {
        return time;
    }


    public void setTime(Timestamp time) {
        this.time = time;
    }

    @Basic
    @Column(name = "deadline")
    public Timestamp getDeadline() {
        return deadline;
    }

    public void setDeadline(Timestamp deadline) {
        this.deadline = deadline;
    }

    @Basic
    @Column(name = "discussed")
    public Timestamp getDiscussed() {
        return discussed;
    }


    public void setDiscussed(Timestamp time) {
        this.discussed = time;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    public TicketState getState() {
        return state;
    }

    public void setState(TicketState state) {
        this.state = state;
    }

    @Basic
    @Column(name = "state_time")
    public Timestamp getStateTime() {
        return stateTime;
    }

    public void setStateTime(Timestamp stateTime) {
        this.stateTime = stateTime;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned")
    public User getAssigned() {
        return assigned;
    }

    public void setAssigned(User assigned) {
        this.assigned = assigned;
    }

    @OneToMany(mappedBy = "ticket", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    public Collection<Message> getMessages() {
        return messages;
    }

    public void setMessages(Collection<Message> messages) {
        this.messages = messages;
    }

    @OneToMany(fetch = FetchType.LAZY)
    public Collection<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(Collection<Attachment> attachments) {
        this.attachments = attachments;
    }
    
    @OneToMany(fetch =FetchType.LAZY,mappedBy="ticket", cascade = CascadeType.ALL)
    public List<Vote> getVote() {
		return vote;
	}
	public void setVote(List<Vote> vote) {
		this.vote = vote;
	}

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId=" + ticketId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", state=" + state +
                ", stateTime=" + stateTime +
                ", time=" + time +
                ", deadline=" + deadline +
                ",attachments=" + attachments +
                '}';
    }
}
