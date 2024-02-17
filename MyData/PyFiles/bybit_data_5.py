from pybit.unified_trading import HTTP
import datetime as dt
import pandas as pd
import numpy as np
import time
import math


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
        time.sleep(0.2)
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
def saving_dataframe_as_csv(df, parameters, dataframe_save_folder):
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
    df.to_csv(dataframe_save_folder + name_of_csv + '.csv')
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
    time_in_min = input('the time it takes for the trade to be completed in minutes: ')
    time_in_klines =  bybit_kline_interval_to_ms(time_in_min) // bybit_kline_interval_to_ms(interval)
    return time_in_klines, time_in_min


#находим значение Y для 1-ой строки DataFrame
def findning_answers_for_Y(Dtp, Dsl, actual_price, index, amount_of_klines_in_Y, np_low, np_high):
    # ищем максимальное и минимальное значение на заданном отрезке
    for i in range(index, index + amount_of_klines_in_Y):
        if ((np_low[i]/ actual_price) <= (1 - (Dsl / 100))):
            return 0, i, 0
            break
        elif ((np_high[i]/ actual_price) >= (1 + (Dtp / 100))):
            return 1, i, 0
            break
    return 0, index + amount_of_klines_in_Y - 1, 1

#создаём списки X и Y по уже введённым параметрам
def creating_X_Y(X, Y, df, numbers_of_klines_for_X, input_type, amount_of_klines_in_Y, Dtp, Dsl, interval):
    i = 0
    list_of_open_time = []
    list_of_close_time = []
    list_of_trade_results = []
    up_border = df.shape[0] - amount_of_klines_in_Y
    np_close = df['close'].to_numpy()
    np_low = df['low'].to_numpy()
    np_high = df['high'].to_numpy()
    np_timestamp = df['timestamp'].to_numpy()
    while i < up_border:
        if i < numbers_of_klines_for_X[0]:
            i += 1
        else:
            #цена на момент открытия сделки:
            actual_price = np_close[i]

            #подготавливаем значения для списка X:
            #параллельный ввод
            if input_type == 0:
                previous_values = []
                for j in numbers_of_klines_for_X:
                    percentage_difference = (actual_price - np_close[i-j]) / actual_price * 100
                    previous_values.append(round(percentage_difference, 3))

            #последовательный ввод
            elif input_type == 1:
                previous_values = []
                previous_price = actual_price
                for j in numbers_of_klines_for_X:
                    percentage_difference = (previous_price - np_close[i-j]) / actual_price * 100
                    previous_values.append(round(percentage_difference, 3))
                    previous_price = np_close[i-j]



            if previous_values == []:
                print('previous_values is empty!!!')
                break

            # заполняем списки X и Y:
            index = i
            X.append(previous_values)
            value_of_Y, close_kline_number, need_real_price = findning_answers_for_Y(Dtp, Dsl, actual_price,
                                                             index, amount_of_klines_in_Y, np_low, np_high)
            Y.append(value_of_Y)
            close_time = np_timestamp[close_kline_number]
            list_of_open_time.append(np_timestamp[i])
            close_time = close_time + bybit_kline_interval_to_ms(interval)
            list_of_close_time.append(close_time)
            if need_real_price == 1:
                delta_in_price = (np_close[close_kline_number] - np_close[i]) / np_close[i] * 100
                list_of_trade_results.append(round(delta_in_price, 3))
            else:
                list_of_trade_results.append('-')
            i += 1
    return X, Y, list_of_open_time, list_of_close_time, list_of_trade_results


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
def get_data_from_bybit_by_params(parameters, session, dataframe_save_folder):
    #Создание и вывод dataframe
    df = creating_dataframe(parameters, session)
    print(df, '\n')
    #Добавим столбец diffInPercent(DiP) в нашу таблицу, для того чтобы не считать это в будущем
    df['DiP'] = df.apply(lambda x: diffInPercent(x.open, x.close), axis = 1)
    #Сохраняем df в csv файл добавляя к нему столбец utc даты
    name_of_csv = saving_dataframe_as_csv(df, parameters, dataframe_save_folder)
    return name_of_csv, df






def make_and_save_datasets_X_Y(name_of_df, interval, Dtp, Dsl,
                               string_of_klines_for_X, input_type,
                               amount_of_klines_in_Y, time_in_min, df, dataset_save_folder):
    klines_for_X = make_a_list_of_X_klines_in_ms(string_of_klines_for_X)

    numbers_of_klines_for_X = converting_klines_for_X_to_the_base_interval(klines_for_X, interval)
    X = []
    Y = []
    X, Y, list_of_open_time, list_of_close_time, list_of_trade_results  = creating_X_Y(X, Y, df, numbers_of_klines_for_X,
                                                                input_type, amount_of_klines_in_Y,
                                                                Dtp, Dsl, interval)
    testing_df = pd.DataFrame(X)
    # columns_new_names = []
    # for i in range(testing_df.shape[1]):
    #     columns_new_names.append('column_'+str(i))
    #testing_df.columns = columns_new_names
    testing_df.insert(loc = testing_df.shape[1], column = 'Y', value = Y)
    testing_df.insert(loc = testing_df.shape[1], column = 'open_time', value = list_of_open_time)
    testing_df.insert(loc = testing_df.shape[1], column = 'close_time', value = list_of_close_time)
    testing_df.insert(loc = testing_df.shape[1], column = 'trade_results', value = list_of_trade_results)
    if input_type == 1:
        input_type = 'consistent'
    elif input_type == 0:
        input_type = 'parallel'
    name_of_datasets_X_Y = (name_of_df + '&' + str(Dtp) + '_' + str(Dsl) + '_'
                            + string_of_klines_for_X.replace(' ','_',50) + '_'
                            + time_in_min + '_' + input_type)
    testing_df.to_csv(dataset_save_folder + name_of_datasets_X_Y + '.csv', index = False)
    return name_of_datasets_X_Y




#вводим параметры для DataFrame из Bybit, получаем его, добавляем столбцы UTC-date и DiP
#и сохраняем DataFrame в .csv
def getting_dataframe_from_bybit_and_saving_into_csv(dataframe_save_folder):
    #Получение и вывод сессии
    key = '1LJco1gBsXxr9gApYa'
    secret = 'lE66uxgaLNPxshQveaDXOvmi1e1gI6G7DaDG'
    session = get_session_from_bybit(key, secret)
    #Получение списка существующих тикеров
    tickers = get_tickers_with_USDT(session)
    amount_of_dataframes = int(input('How much dataframe do you want to get? Enter the quantity: '))
    list_of_parameters = []
    for i in range(amount_of_dataframes):
        list_of_parameters.append(create_parameters_for_dataframe(session, tickers))
        print('----------------------------------------------------------')
    for i in range(amount_of_dataframes):
        print(list_of_parameters[i])
        name_of_csv, df = get_data_from_bybit_by_params(list_of_parameters[i], session, dataframe_save_folder)
        print(name_of_csv + ' \n')
    return



#Вводим параметры для датасетов X и Y, создаём их и сохраняем в CSV файлы с подробными именами
def making_datasests_X_and_Y_from_DataFrames(dataframe_open_folder, dataset_save_folder):
    i = input('Enter the names of the dataframes without .csv that you want to use, separated by a space: ')
    df = pd.read_csv(dataframe_open_folder + i + '.csv')
    separator = i.find('_')
    interval = i[:separator]
    various_intervals = {'1', '3', '5', '15', '30', '60', '120', '240', '360', '720', 'D', 'W', 'M' }
    if interval not in various_intervals:
        interval = input('The program could not recognize the dataframe INTERVAL, enter it manually: ')
    Dtp = float(input('Enter the value of the TAKE PROFIT level as a percentage: '))
    Dsl = float(input('Enter the value of the STOP LOSS level as a percentage: '))
    string_of_klines_for_X = input('THE MINIMUM CANDLE INTERVAL IS NOT LESS THAN THE BASE INTERVAL! Enter the candle intervals used in the bibinet API to compile the X dataset, in the format: 15x4 60x2 D: ')
    input_type = int(input('choose the input_type: consistent - 1, parallel - 0: '))
    amount_of_klines_in_Y, time_in_min = get_amount_of_klines_for_Y(interval)
    name_of_datasets_X_Y = make_and_save_datasets_X_Y(i, interval, Dtp, Dsl, string_of_klines_for_X, input_type, amount_of_klines_in_Y, time_in_min, df, dataset_save_folder)
    print(name_of_datasets_X_Y, ' successfully created!')
    return


#создаем numpy array X и Y для обучения нейросети
def create_numpy_array_for_AI_learning():
    s = list(input('Enter the names of the DATASETS without .csv separated by a space that you want to use (several datasets will be combined):')
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
                if X[i][j] == np.nan:
                    nan_strings.append(i)
                    break
                if X[i][j]=='':
                    nan_strings.append(i)
                    break
        X = np.delete(X, nan_strings, axis = 0)
        Y = np.array(Y)
        for j in range(len(Y)):
            if Y[j] == np.nan:
                nan_strings.append(j)
                break
            if Y[j]=='':
                nan_strings.append(j)
                break

        Y = np.delete(Y, nan_strings, axis = 0)
        name_for_X_and_Y = input('Enter a name for the X and Y arrays: ')
        data = {}
        data = {'X': X, 'Y': Y }
        testing_df = pd.DataFrame(data)
        testing_df.to_csv(name_for_X_and_Y + '.csv')
        print(name_for_X_and_Y, ' READY !')
        return X, Y
    else:
       print('datasets have different parameters')
       return
