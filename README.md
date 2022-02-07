# WIX3004-GOCery

## Introduction

This is the repository for our team (JasonDerulo)'s submission for WIX3004 Mobile Application Development group assignment for Semester 1, 2021/2022 session.

With this application, families can easily list down the items to be bought in a virtual manner, and can include the location of the items, and pictures to ensure the correct items are bought (I.e., types of fish, brands of toilet paper, etc.). This will not only be much more convenient for the family representative to refer to but can also ensure that the correct items are bought. This application also enables the list of groceries to be automatically sent to the representatives when they leave the house by referring to the location of the user.

## Modules & Functions

The following are the list of modules and corresponding functions planned for the application:

| ID |            Module            |                             Function                             |                                                                 Function Brief  Description                                                                 |   Status  |
|:--:|:----------------------------:|:----------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------------------------------------------:|:---------:|
|  1 |            General           | Log in                                                           | Allow users to log into their account and into their own user profiles                                                                                      | Completed |
|    |                              | Log out                                                          | Allow users to logout of their account                                                                                                                      | Completed |
|    |                              | Register account                                                 | Allow users to register new account                                                                                                                         | Completed |
|    |                              | Account settings                                                 | Allows members to update their account details (display name, password)                                                                                     | Completed |
|  2 |    Grocery List Management   | Manage grocery list                                              | Enables users within the household to add/remove items from the grocery list                                                                                | Completed |
|    |                              | Complete Shopping                                                | Enable the user to complete a shopping trip by saving the checked grocery items in a single trip data                                                       | Completed |
|    |                              | Display Completed Shopping Trips                                 | Display to the user all the shopping trip that has been completed                                                                                           | Completed |
|    |                              | Attach image and location                                        | Users can attach images of the groceries, as well as the location or store of where they can be bought                                                      | Completed |
|  3 | Household Account Management | Manage user profiles                                             | Enables the admin to add, remove, edit user profiles (members) from the household. Admins can also appoint the members as household representatives.        | Completed |
|  4 |          Receipt OCR         | Scan receipts                                                    | Scan receipts from images taken with the camera or directly taken from the gallery.                                                                         | Completed |
|    |                              | Extract information from receipts using OCR                      | Information such as the total cost, items, cost per item, etc. are extracted from receipts.                                                                 | Completed |
|  5 |        Expense Tracker       | Manage (add, update, view, delete) expense records               | Enable user to add expense records such as their name, purchase date, type and total cost.                                                                  | Completed |
|    |                              | View overview of expenses                                        | Visualise expense records overview based on expense types in a chart manner                                                                                 | Completed |
|  5 |   Card & Voucher Management  | Manage household member card                                     | Enable user to add and remove member card information for purchases at official stores. Therefore, the whole family can share them.                         | Completed |
|    |                              | Attach gift vouchers or discounts                                | Enable user to add available vouchers and discounts. The used and expired vouchers will be automatically archived.                                          | Completed |
|  6 |   Route Planning Management  | Display graphical map.                                           | Displays an interactive map for the user.                                                                                                                   | Completed |
|    |                              | Display shortest path to travel between multiple grocery shops.  | Users will be able to list out the grocery shops that they plan to visit, and the application will display the recommended route and estimated time taken.  | Completed |
|    |                              | Open external applications to route user                         | If user needs turn-by-turn navigation, the application will offer to open an external application such as Google Map to guide the user straight away.       | Completed |
## Project Background & Motivation

In November 2021, Malaysians nationwide are met with concerning news about the rising prices of groceries and various food items, especially wet goods such as vegetables and poultry. According Consumer Association of Penang (CAP), vegetables saw the highest hike in prices of up to 200 percent or threefold over the past two weeks due to several factors such as inclement weather, inflation and labour shortage.  Even Malaysians’ favourite local bread brand Gardenia went viral on social media for the purported price increase of its products. News of higher product prices are a sign of things to come for the consumer goods sector, following an increase in commodity prices and transport charges amid the resumption of economic activities globally as COVID-19 becomes endemic in more countries. Whether this is a temporary phenomenon or a long-term one, Malaysians are sure to become more attentive to their regular grocery expenses.

Furthermore, during the Movement Control Order (MCO), Malaysians are restricted from going out in which only one representative per household is allowed to go out to buy basic necessities. This led to families having to manually write down lists of things to buy either on pieces of paper, or through messaging platforms like WhatsApp which is tedious and time-consuming. Although the country has long since moved on from the lockdown phases, it is still highly encouraged that families not only plan their purchases by having a list of items to buy to avoid wasting time in the supermarket, but also send only representatives of each household to purchase groceries or buy daily necessities at nearby supermarkets or grocery stores to minimize congestion, spread and risk of contracting COVID-19.

## Problem Statements

### i.	Household Account Management

When managing grocery lists, keeping track of who adds, removes or edits items in the list can be tedious, especially for big families. This would lead to unnecessary items cluttering the list, causing the family representative to overlook more important items in the list when buying groceries. 

### ii.	Grocery List Management

Managing grocery list can be difficult without proper planning, especially when it’s a big household. Grocery items are constantly being added, remove or edit sporadically as household members remembers them at any notice. Moreover, they may jot the items at different places, increasing mix-ups, redundancies or forgotten item. Items added may also be ambiguous in nature or the location to be bought is unknown, making the grocery trip more of a hassle and need confirmation from the requester. Speaking of which, there are some cases where the requestor should not be requesting specific items. Example of this is children wanting sweets. Not knowing who added which may lead in some household rules being broken.

### iii.	Receipt OCR & Expense Tracker

Grocery shopping these days has become a tedious job. The user needs to continuously monitor groceries at home and also has the work of managing coupons, maintaining shopping lists, standing in restraint out queues, reading the fine print on food cans and receipt for Tracker their expenses. Most people recognise the importance of keeping receipts for various reasons such as for expense tracker. The problem with paper receipts is that not only is it easy to lose such an important document, but compiling them in large quantities becomes difficult for the user to browse and refer to their previous purchase history as well as hard to be carried around. 

While there are plenty of existing expense tracker applications in the market which allows users to digitally track their expenses, most rely on the user manually keying in the data which can be time-consuming and inconvenient. For the small minority of applications that allow users to capture and convert images of receipts into PDF format to be stored inside the application, they do not possess the ability to automatically extract key information such as the price of each item and total price and integrate them directly inside the budget-Tracker functionality of the application.

### iv.	Card & Voucher Management

Most shoppers will prefer to use member cards and vouchers in order to spend wisely. Various stores offer different kind of loyalty rewards and bonus points; thus, it will be hard to manage the card or voucher in one grocery list that will include more than one stores. It will be more difficult when only one person the household own the card and voucher – the other members do not have instant access to use them when paying for the groceries.

### v.	Route Planning Management

As everyone’s time is precious, part of managing a shopping grocery list is also planning out which place to go first. Not every store would carry the same item, so more often than not people would have to visit multiple stores in a single complete shopping trip. In this case, planning out the quickest routes is vital to save precious time in buying groceries. At the same time, if a store does not carry a specific item, or it is closed, it would be a massive hassle to think of a different store and figure out whether it would be wise to go shop for other items first or go to an alternate store to get the missing item.

## Objectives

The following are the objectives of our proposed mobile application:
1.	To develop an Android mobile application where users can manage their shopping lists efficiently through individual user profiles
2.	To develop an Android mobile application that allows users to provide users with a centralized grocery list which can be viewed and modified by all family members 
3.	To develop an Android mobile application for extracting key information from scanned grocery receipts and track grocery expenses
4.	To develop an Android mobile application that enable users to share loyalty cards and store coupons usage among the same household.
5.	To develop an Android mobile application that is able to guide users to plan out their grocery trips based on their grocery list for the day

