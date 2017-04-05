/*************************************************************************
*                                                          Create Tables *
*************************************************************************/

CREATE TABLE movie_directors (
	movieID					NUMBER	NOT NULL,
	directorID				VARCHAR(150)	NOT NULL,
	directorName			VARCHAR(150)	NOT NULL
);

CREATE TABLE user_taggedmovies_timestamps (
	userID					NUMBER	NOT NULL,
	movieID					NUMBER	NOT NULL,
	tagID					NUMBER	NOT NULL,
	timestamp				NUMBER	NOT NULL
);

CREATE TABLE movie_countries (
	movieID					NUMBER	NOT NULL,
	country					VARCHAR(150)
);

CREATE TABLE user_taggedmovies (
	USERID					NUMBER	NOT NULL,
	MOVIEID					NUMBER	NOT NULL,
	TAGID					NUMBER	NOT NULL,
	DATE_Day				NUMBER	NOT NULL,
	DATE_Month				NUMBER	NOT NULL,
	DATE_Year				NUMBER	NOT NULL,
	DATE_Hour				NUMBER	NOT NULL,
	DATE_Minute				NUMBER	NOT NULL,
	DATE_Second				NUMBER	NOT NULL
);

CREATE TABLE movies (
	ID						NUMBER	NOT NULL,
	title					VARCHAR(150)	NOT NULL,
	imdbID					NUMBER	NOT NULL,
	spanishTitle			VARCHAR(150)	NOT NULL,
	imdbPictureURL			VARCHAR(150),
	year					NUMBER	NOT NULL,
	rtID					VARCHAR(150),
	rtAllCriticsRating		NUMBER	NOT NULL,
	rtAllCriticsNumReviews	NUMBER	NOT NULL,
	rtAllCriticsNumFresh	NUMBER	NOT NULL,
	rtAllCriticsNumRotten	NUMBER	NOT NULL,
	rtAllCriticsScore		NUMBER	NOT NULL,
	rtTopCriticsRating		NUMBER	NOT NULL,
	rtTopCriticsNumReviews	NUMBER	NOT NULL,
	rtTopCriticsNumFresh	NUMBER	NOT NULL,
	rtTopCriticsNumRotten	NUMBER	NOT NULL,
	rtTopCriticsScore		NUMBER	NOT NULL,
	rtAudienceRating		NUMBER	NOT NULL,
	rtAudienceNumRatings	NUMBER	NOT NULL,
	rtAudienceScore			NUMBER	NOT NULL,
	rtPictureURL			VARCHAR(150)	NOT NULL
);

CREATE TABLE movie_tags (
	movieID					NUMBER	NOT NULL,
	tagID					NUMBER	NOT NULL,
	tagWeight				NUMBER	NOT NULL
);

CREATE TABLE movie_actors (
	movieID					NUMBER	NOT NULL,
	actorID					VARCHAR(150)	NOT NULL,
	actorName				VARCHAR(150),
	ranking					NUMBER	NOT NULL
);

CREATE TABLE movie_genres (
	movieID					NUMBER	NOT NULL,
	genre					VARCHAR(150)	NOT NULL
);

CREATE TABLE movie_locations (
	movieID					NUMBER	NOT NULL,
	location1				VARCHAR(150),
	location2				VARCHAR(150),
	location3				VARCHAR(150),
	location4				VARCHAR(150)
);

CREATE TABLE user_ratedmovies_timestamps (
	userID					NUMBER	NOT NULL,
	movieID					NUMBER	NOT NULL,
	rating					NUMBER	NOT NULL,
	timestamp				NUMBER	NOT NULL
);

CREATE TABLE user_ratedmovies (
	userID					NUMBER	NOT NULL,
	movieID					NUMBER	NOT NULL,
	rating					NUMBER	NOT NULL,
	date_day				NUMBER	NOT NULL,
	date_month				NUMBER	NOT NULL,
	date_year				NUMBER	NOT NULL,
	date_hour				NUMBER	NOT NULL,
	date_minute				NUMBER	NOT NULL,
	date_second				NUMBER	NOT NULL
);

CREATE TABLE tags (
	id						NUMBER	NOT NULL,
	value					VARCHAR(150)	NOT NULL
);

