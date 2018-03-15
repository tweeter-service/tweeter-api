package com.example;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
public class TweeterController {
	private final JdbcTemplate jdbcTemplate;
	private final User user;

	public TweeterController(JdbcTemplate jdbcTemplate, User user) {
		this.jdbcTemplate = jdbcTemplate;
		this.user = user;
	}

	@GetMapping("v1/timelines")
	public List<Tweet> getTimelines() {
		return jdbcTemplate.query(
				"SELECT tweet_id, text, username, created_at FROM tweets ORDER BY created_at DESC",
				tweetRowMapper);
	}

	@GetMapping("v1/tweets")
	public List<Tweet> tweets() {
		String username = this.user.getUsername();
		return jdbcTemplate.query(
				"SELECT tweet_id, text, username, created_at FROM tweets WHERE username=? ORDER BY created_at DESC",
				tweetRowMapper, username);
	}

	@PostMapping("v1/tweets")
	public Tweet postTweets(@RequestBody Tweet tweet) {
		tweet.tweetId = UUID.randomUUID();
		tweet.createdAt = new Date();
		tweet.username = this.user.getUsername();
		jdbcTemplate.update(
				"INSERT INTO tweets(tweet_id, text, username, created_at) VALUES (?, ?, ?, ?)",
				tweet.tweetId.toString(), tweet.text, tweet.username, tweet.createdAt);
		return tweet;
	}

	static class Tweet {
		public UUID tweetId;
		public String text;
		public String username;
		public Date createdAt;
	}

	RowMapper<Tweet> tweetRowMapper = new RowMapper<Tweet>() {
		@Override
		public Tweet mapRow(ResultSet rs, int i) throws SQLException {
			Tweet tweet = new Tweet();
			tweet.tweetId = UUID.fromString(rs.getString("tweet_id"));
			tweet.text = rs.getString("text");
			tweet.username = rs.getString("username");
			tweet.createdAt = new Date(rs.getTimestamp("created_at").getTime());
			return tweet;
		}
	};
}
