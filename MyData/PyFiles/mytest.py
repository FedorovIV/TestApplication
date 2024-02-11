# Импортирование необходимых библиотек
import pandas as pd
import numpy as np
import tensorflow as tf
from binance.client import Client

# Получение доступа к API биржи бинанс
api_key = 'ваш_api_ключ'
api_secret = 'ваш_api_секрет'
client = Client(api_key, api_secret)

# Сбор данных о криптовалютах и их изменении цен за определенный период времени
bars = client.get_historical_klines("BTCUSDT", Client.KLINE_INTERVAL_1DAY, "30 days ago UTC")

# Подготовка данных для обучения нейросети
data = pd.DataFrame(bars, columns=['timestamp', 'open', 'high', 'low', 'close', 'volume', 'close_time', 'quote_asset_volume', 'number_of_trades', 'taker_buy_base_asset_volume', 'taker_buy_quote_asset_volume', 'ignore'])
data.drop(columns=['close_time', 'quote_asset_volume', 'number_of_trades', 'taker_buy_base_asset_volume', 'taker_buy_quote_asset_volume', 'ignore'], inplace=True)
data['timestamp'] = pd.to_datetime(data['timestamp'], unit='ms')
data.set_index('timestamp', inplace=True)
data = data.astype(float)

# Создание модели нейросети
model = tf.keras.Sequential([
    tf.keras.layers.Dense(64, activation='relu', input_shape=(5,)),
    tf.keras.layers.Dense(32, activation='relu'),
    tf.keras.layers.Dense(1)
])

# Компиляция модели
model.compile(optimizer='adam',
              loss='mse')

# Обучение модели на подготовленных данных
model.fit(data.iloc[:-10], data['close'].iloc[:-10], epochs=50)

# Проверка качества работы нейросети на тестовых данных
model.evaluate(data.iloc[-10:], data['close'].iloc[-10:])

# Использование нейросети для принятия решений о покупке или продаже криптовалют на бирже бинанс
prediction = model.predict(data.iloc[-1:].values)
if prediction > data['close'].iloc[-1]:
    print('Купить')
else:
    print('Продать')