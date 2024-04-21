# Backtesting

Overview-
Backtest is an Android application designed to provide users with in-depth analysis and tracking of cryptocurrency trading strategies. The app allows users to visualize their trading results through detailed charts and manage their favorite strategies for quick reference.

Features-
Home Activity: The landing page after login, displaying the most recent trading chart if available, or a welcome message.

Favorite Activity: Users can view and manage their favorite trading strategies and results.

History Activity: This feature provides a historical view of past trades, allowing users to analyze performance over time.

Test Activity: Allows users to simulate trading strategies with historical data to forecast potential outcomes without financial risk.

Top Activity: Displays the top performing strategies based on user data and shared insights.




Technical Components:

HomePageActivity-
The central hub of the application, presenting the most recent chart data or general information if no data is available. It includes navigation to other parts of the app and the option to sign out.

FileHandler-
Handles file interactions within the app, especially focused on reading and writing data necessary for the functioning of trading strategies and result storage.

StrategyExecutor-
A crucial component responsible for the execution of cryptocurrency trading strategies. It uses historical data to simulate the trading environment and calculates performance metrics.

TradeStrategies-
Defines and manages various trading strategies. This class is vital for users who wish to test different approaches to trading within the app.

Libraries and Tools:
Firebase: Used for authentication, database storage, and real-time data handling.
MPAndroidChart: A powerful library for rendering complex charts within Android applications, used extensively for displaying trading analytics.

