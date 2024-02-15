from pybit.unified_trading import HTTP
import datetime as dt
import pandas as pd
import numpy as np
import time
import math
from pathlib import Path  

#Получение сессии от bybit
def get_session_from_bybit(key, secret):
  session = HTTP(api_key=key, api_secret=secret, testnet=False)
  return session


#Получаем список доступных символов
def get_tickers_with_USDT(session):
    result = session.get_tickers(
                    category="spot").get('result')['list']

    tickers = [asset['symbol'] for asset in result]
    return tickers

#Ввод запрашивемой валютной пары(symbol)
def get_symbol(tickers):
    while True:
      symbol = input('Enter the currency USDT pair symbol, like BTCUSDT: ')
      if symbol not in tickers:
        print('Your symbol is incorrect, please try again')
      else:
        return symbol
        break

#Запрашиваемый интервал свечей(interval) various_intervals - это подходящие нам интервалы
def get_kline_interval():
    various_intervals = {'1', '3', '5', '15', '30', '60', '120', '240', '360', '720', 'D', 'W', 'M' }
    while True:
      interval = input('Enter the klines interval, choose from this variants 1 3 5 15 30 60 120 240 360 720 D W M: ')
      if interval not in various_intervals:
        print('Your interval is incorrect, please try again')
      else:
        return interval
        break

#Запрашиваемая дата для диапазона. Результат в милисекундах(date_in_ms)
def get_date_in_ms(starting_or_ending):
    instruction = starting_or_ending + ' date sep. by spaces, like: 2024 1 20 or 2024 1 20 17 56: '
    date_in_ms = list(map(int,
                input(instruction).split()))
    date_in_ms = date_in_ms + [0]*(6 - len(date_in_ms))
    date_in_ms = int(dt.datetime(date_in_ms[0], date_in_ms[1], date_in_ms[2],
                             date_in_ms[3], date_in_ms[4], date_in_ms[5]).timestamp())*1000
    return date_in_ms





#Получаем ответ от Bybit
def get_response_from_API(symbol, interval, starting_date, ending_date, session):
    response = session.get_kline(category = 'spot',
                             symbol = symbol,
                             interval = interval,
                            start = starting_date,
                            end = ending_date
                            ).get('result')
    return response

#Функция, которая дает нам окончание
def get_last_timestamp(df):
    return int(df.timestamp[0:].values[0])


#Функция, которая превращает response в удобный для работы dataframe
def format_data(response, last_index):
    data = response.get('list', None)
    data = pd.DataFrame(data,
                        columns = [
                            'timestamp',
                            'open',
                            'high',
                            'low',
                            'close',
                            'volume',
                            'turnover'
                            ],
                        )
    data.index = last_index + data.index
    return data[::-1].apply(pd.to_numeric)


#Получение и создание многоблочного dataframe
def creating_dataframe(parameters, session):
    symbol = parameters['symbol']
    interval = parameters['interval']
    starting_date = parameters['starting_date']
    ending_date = parameters['ending_date']
    df = pd.DataFrame()
    last_index = 0
    difference_in_time = ending_date - starting_date
    amount_of_200_blocks = (difference_in_time // bybit_kline_interval_to_ms(interval))//200
    while True:
        if ending_date <= starting_date:
            break
        response = get_response_from_API(symbol, interval, starting_date, ending_date, session)
        latest = format_data(response, last_index)
        if not isinstance(latest, pd.DataFrame):
            break
        try:
            ending_date = get_last_timestamp(latest)
        except:
            break
        print('Progress in getting blocks: ', last_index//200, '/',  amount_of_200_blocks)
        time.sleep(0.1)
        df = pd.concat([latest,df])
        if len(latest) == 1: break
        last_index += 200

    #Удаляем дубликаты от соединения 200-ток
    df.drop_duplicates(subset=['timestamp'], keep='last', inplace=True)
    # переделываем индексы с учетом удаленных дубликатов
    df.index = range(df.shape[0])
    return df

#Разность между ценой открытия и закрытия одной свечи в процентах
def diffInPercent(opened, closed):
    diff = (opened - closed) / opened * 100
    diff = f"{diff:.{3}f}"
    return(diff)


#Сохранение готового DataFrame под уникальным именем в виде csv файла
#Столбец индексов удалить нельзя, и он продублируется при последующем открытии таблицы,
#поэтому мы заполним его полезной инфой:
def saving_dataframe_as_csv(df, parameters):
    symbol = parameters['symbol']
    interval = parameters['interval']
    starting_date = parameters['starting_date']
    ending_date = parameters['ending_date']
    st = dt.datetime.utcfromtimestamp(starting_date//1000)
    starting_date = str(st.year) + '_' + str(st.month) + '_' + str(st.day)
    st = dt.datetime.utcfromtimestamp(ending_date//1000)
    ending_date = str(st.year) + '_' + str(st.month) + '_' + str(st.day)
    f = lambda x: dt.datetime.utcfromtimestamp(int(x)/1000)
    df.index = df.timestamp.apply(f)
    df.index.name = 'utc_date'
    # создаем csv файл
    name_of_csv = (interval + '_' + symbol + '_' + starting_date + '_' + ending_date)

    print("Please enter the path where you want to save the csv file")
    
    path = input()

    df.to_csv(path +"\\"+ name_of_csv + '.csv')
    print(df)
    return name_of_csv


#Проверка на наличие проскальзываний, возвращающая индексы проскальзываний
def gaps_searching(df):
    gaps = []
    for i in range(df.shape[0]):
        if i > 0:
            if df.loc[i, 'open'] !=  df.loc[i-1, 'close']:
                gaps.append(i)
            elif df.loc[i, 'timestamp'] - 60000 != df.loc[i-1, 'timestamp']:
                gaps.append(i)
    return(gaps)




#Переводим интервал свечи в милисекунды
def bybit_kline_interval_to_ms(interval):
    letters = {'D', 'W', 'M'}
    if interval in letters:
        if interval == 'D':
            interval_in_ms = 86400000
        elif interval == 'W':
            interval_in_ms = 604800000
        elif interval == 'M':
            interval_in_ms = 2592000000 #30 дней
    else:
        interval_in_ms = int(interval) * 60000
    return interval_in_ms

#создаем значение в милисекундах и количество свечей для каждого введенного интервала в X
def get_amount_and_type_of_klines_before(s):
    separator = s.find('x')
    if separator != -1:
        kline = bybit_kline_interval_to_ms(s[:separator])
        amount_of_klines = int(s[separator + 1:])
    else:
        kline = bybit_kline_interval_to_ms(s)
        amount_of_klines = 1
    return kline, amount_of_klines


#создаем массив со значениями в милисекундах и количеством требуемых свечей для X
def make_a_list_of_X_klines_in_ms(string_of_klines_for_X):
    splited_klines = string_of_klines_for_X.split(' ')
    klines_for_X = []
    for i in splited_klines:
        klines_for_X.append(list(get_amount_and_type_of_klines_before(i)))
    return klines_for_X


#находим значения klines для X на базовом интервале interval
def converting_klines_for_X_to_the_base_interval(klines_for_X, interval):
    non_sorted_X_intervals = []
    interval_in_ms = bybit_kline_interval_to_ms(interval)
    for i in range(len(klines_for_X)):
        for j in range(klines_for_X[i][1]):
            non_sorted_X_intervals.append((klines_for_X[i][0] * (j + 1)) // interval_in_ms)
    non_sorted_X_intervals = set(non_sorted_X_intervals)
    non_sorted_X_intervals = list(non_sorted_X_intervals)
    non_sorted_X_intervals.sort()
    return non_sorted_X_intervals


#вводим время в минутах, за которое сделка должна быть закрыта
def get_amount_of_klines_for_Y(interval):
    time_in_min = input('введите время, за которое сделка должна быть завершена в минутах: ')
    time_in_klines =  bybit_kline_interval_to_ms(time_in_min) // bybit_kline_interval_to_ms(interval)
    return time_in_klines


#находим значение Y для 1-ой строки DataFrame
def findning_answers_for_Y(Dtp, Dsl, df, actual_price, index, amount_of_klines_in_Y):
    # ищем максимальное и минимальное значение на заданном отрезке
    lowest_value = df.loc[index :index + amount_of_klines_in_Y].low.min()
    highest_value = df.loc[index :index + amount_of_klines_in_Y].high.max()
    if (1 + (Dtp / 100)) > highest_value / actual_price:
        return 0
    elif (1 - (Dsl / 100)) <= lowest_value / actual_price:
        return 1
    else:
        for i in range(index, index + amount_of_klines_in_Y):
            if ((df.iloc[i].low / actual_price) <= (1 - (Dsl / 100))):
                return 0
                break
            elif ((df.iloc[i].high / actual_price) >= (1 + (Dtp / 100))):
                return 1
                break

#создаём списки X и Y по уже введённым параметрам
def creating_X_Y(X, Y, df, numbers_of_klines_for_X, amount_of_klines_in_Y, Dtp, Dsl):
    i = 0
    while i < df.shape[0] - amount_of_klines_in_Y:
        if i < numbers_of_klines_for_X[0]:
            i += 1
        else:
            # подготавливаем значения для списка X:
            previous_values = []
            for j in numbers_of_klines_for_X:
                previous_values.append(df.iloc[i - j].DiP)

            #цена на момент открытия сделки:
            actual_price = df.loc[i].open

            if previous_values == []:
                print('previous_values is empty!!!')
                break

            # заполняем списки X и Y:
            index = i
            X.append(previous_values)
            Y.append(findning_answers_for_Y(Dtp, Dsl, df, actual_price, index, amount_of_klines_in_Y))
            i += 1
    return X, Y


#создаём и вводим параметры для dataframe
#yf ds[jlt gjkexftv gfhfvtnhs]
def create_parameters_for_dataframe(session, tickers):
    #Получение параметров для создания dataframe
    symbol = get_symbol(tickers)
    interval = get_kline_interval()
    starting_date = get_date_in_ms('starting')
    ending_date = get_date_in_ms('ending')
    parameters = {'symbol' : symbol,
                 'interval' : interval,
                 'starting_date' : starting_date,
                 'ending_date' : ending_date}
    return parameters



#получаем данные от bybit по уже имеющимся параметрам + добавляем столбец DiP
#на выходе возвращает имя .csv файла и готовый DataFrame с данными со столбцом DiP
def get_data_from_bybit_by_params(parameters, session):
    #Создание и вывод dataframe
    df = creating_dataframe(parameters, session)
    print(df, '\n')
    #Добавим столбец diffInPercent(DiP) в нашу таблицу, для того чтобы не считать это в будущем
    df['DiP'] = df.apply(lambda x: diffInPercent(x.open, x.close), axis = 1)
    #Сохраняем df в csv файл добавляя к нему столбец utc даты
    name_of_csv = saving_dataframe_as_csv(df, parameters)
    return name_of_csv, df






def make_and_save_datasets_X_Y(name_of_df, interval, Dtp, Dsl, string_of_klines_for_X, amount_of_klines_in_Y, df):
  klines_for_X = make_a_list_of_X_klines_in_ms(string_of_klines_for_X)

  numbers_of_klines_for_X = converting_klines_for_X_to_the_base_interval(klines_for_X, interval)
  X = []
  Y = []
  X, Y = creating_X_Y(X, Y, df, numbers_of_klines_for_X, amount_of_klines_in_Y, Dtp, Dsl)
  data = {}
  data = {'X': X, 'Y': Y }
  testing_df = pd.DataFrame(data)
  name_of_datasets_X_Y = (name_of_df + '&' + str(Dtp) + '_' + str(Dsl) + '_'
                          + string_of_klines_for_X.replace(' ','_',50))
  testing_df.to_csv(name_of_datasets_X_Y + '.csv')
  return name_of_datasets_X_Y




#вводим параметры для DataFrame из Bybit, получаем его, добавляем столбцы UTC-date и DiP
#и сохраняем DataFrame в .csv
def getting_dataframe_from_bybit_and_saving_into_csv():
    #Получение и вывод сессии
    key = '1LJco1gBsXxr9gApYa'
    secret = 'lE66uxgaLNPxshQveaDXOvmi1e1gI6G7DaDG'
    session = get_session_from_bybit(key, secret)
    #Получение списка существующих тикеров
    tickers = get_tickers_with_USDT(session)
    amount_of_dataframes = int(input('Сколько dataframe вы хотите получить? Введите количество: '))
    list_of_parameters = []
    for i in range(amount_of_dataframes):
        list_of_parameters.append(create_parameters_for_dataframe(session, tickers))
        print('----------------------------------------------------------')
    for i in range(amount_of_dataframes):
        print(list_of_parameters[i])
        name_of_csv, df = get_data_from_bybit_by_params(list_of_parameters[i], session)
        print(name_of_csv + ' \n')
    return



#Вводим параметры для датасетов X и Y, создаём их и сохраняем в CSV файлы с подробными именами
def making_datasests_X_and_Y_from_DataFrames():
    s = list(input('Введите через пробел названия датафреймов без .csv, которыми вы хотите воспользоваться: ')
             .split(' '))
    same_params_for_all = 0
    for i in s:
      df = pd.read_csv(i + '.csv')
      if same_params_for_all != 1:
        separator = i.find('_')
        interval = i[:separator]
        various_intervals = {'1', '3', '5', '15', '30', '60', '120', '240', '360', '720', 'D', 'W', 'M' }
        if interval not in various_intervals:
          interval = input('Программа не смогла распознать интервал датафрейма, введите его в ручную: ')
        Dtp = float(input('Введите значение уровня take profit в процентах: '))
        Dsl = float(input('Введите значение уровня stop loss в процентах: '))
        string_of_klines_for_X = input('МИНИМАЛЬНЫЙ ИНТЕРВАЛ СВЕЧИ НЕ МЕНЬШЕ БАЗОВОГО ИНТЕРВАЛА! Введите интервалы свечей используемые в API bybit для составления датасета X, в формате:  15x4 60x2 D: ')
        amount_of_klines_in_Y = get_amount_of_klines_for_Y(interval)
        same_params_for_all = int(input('Использовать те же параметры для оставшихся датасетов 1 - да, 0 - нет: '))
      name_of_datasets_X_Y = make_and_save_datasets_X_Y(i, interval, Dtp, Dsl, string_of_klines_for_X, amount_of_klines_in_Y, df)
      print(name_of_datasets_X_Y, ' успешно создан!')
    return


#создаем numpy array X и Y для обучения нейросети
def create_numpy_array_for_AI_learning():
    s = list(input('Введите через пробел названия датасетов без .csv, которыми вы хотите воспользоваться (несколько датасетов будут объединины): ')
             .split(' '))
    parametres = s[0][s[0].find('&'):]
    all_are_the_same = True
    for i in range(1, len(s)-1):
        if s[i][s[i].find('&'):] != parametres:
            all_are_the_same = False
    if all_are_the_same == True:
        df = pd.DataFrame()
        for i in s:
            latest = pd.read_csv(i + '.csv')
            df = pd.concat([latest, df])
        df.index = range(df.shape[0])
        X = df['X'].values.tolist()
        Y = df['Y'].values.tolist()
        X = np.array(X)
        nan_strings = []
        for i in range(len(X)):
            for j in range(len(X[i])):
                if j == np.nan:
                    nan_strings.append(i)
                    break
        X = np.delete(X, nan_strings, axis = 0)
        Y = np.array(Y)
        Y = np.delete(Y, nan_strings, axis = 0)
        name_for_X_and_Y = input('Введите имя для массивов X и Y: ')
        data = {}
        data = {'X': X, 'Y': Y }
        testing_df = pd.DataFrame(data)
        testing_df.to_csv(name_for_X_and_Y + '.csv')
        print(name_for_X_and_Y, ' готов!')
        return X, Y
    else:
       print('у датасетов разные параметры')
       return
