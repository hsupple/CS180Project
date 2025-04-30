# CS18000 Auction House Team Project

Program utilizing Java interfaces and classes to ultimately create an interactive Java Application where a user may list auctions and bid on other item listings.

## Features
- Create Buyer Account
- Create Seller Account
- Password protected user accounts
- Creating auction listings with timers, buy now prices, and bidding options
- Buyer accounts may bid on auctions or use the buy it now option to purchase an item as a seller-determined price.
- Database to contain all listings and user accounts
- Search functionality for both Users and Item listings
- Messaging between Buyers and Sellers
- Account deletion && Authorized Password Changes

## Software Architecture
- Uses Client <-> Server relationship between objects to communicate with and take from database.
- Clients may interact with each other using messages sent over server which includes solely thread and file safe operations.
- All data is hard-stored within .txt files only accessible through requests sent through the server.

## Installation
- Change to desired Directory
- Clone the Repo
- $ git clone https://github.com/hsupple/CS180Project.git

## Test Cases
- Ensure you have a JUnit test case extension on your IDE
- Ensure Server is running while JUnit tests are activated

## How to run the application
Open a terminal and navigate to preferred Directory (dir).

- First run:
- $ git clone https://github.com/hsupple/CS180Project.git
- This will copy the Git repository into your current dir

- To start the application, navigate to the CS180Project dir, then run:
- $ cd src/serverclient && java AuctionServer

- Then in a second terminal in the CS180Project dir run:
- $ cd src/gui && java LoginGUI

- To Create an Account:
- Press "Create Account" and add details such as username, password, and user type.

- Click "Submit" and log in with saved details

## Submission Responsibility

Hayden Supple – Submitted final project to Vocareum.

Hayden Supple – Submitted project report to Brightspace.

## Class Descriptions

AuctionClient – Handles all server communication via Network IO. Allows clients to query listings, make bids, send messages, etc.

Buyer / Seller – Represents a user account with persistent login credentials. Methods for messaging, bidding, and account management.

buyergui – Main GUI for buyers. Displays live auctions, search interface, bid and buy-now functionality, logout/delete account.

searchgui – Display filtered listings based on user search query, enables messaging sellers and rating them.

messagesgui – Displays list of conversations. Users can search, open, and start new conversations.

newmessage – Popup window for composing and sending a message to a seller.

rating – Allows buyers to rate sellers from 0.0 to 5.0.

LoginGUI – Login screen to access buyer or seller interface.

newacct – GUI for creating new accounts with input validation.

# Authors
- @Phaynes742
- @hsupple
- @jburkett013
- @addy-ops
