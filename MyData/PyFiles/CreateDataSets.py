'''
Документация по модулю bybit_data
Модуль добавляется с помощью команды import bybit_data
Главные функции модуля:
    I) getting_dataframe_from_bybit_and_saving_into_csv()
    Что она делает?
        1) Спрашивает, сколько вам нужно DataFrame'ов, после чего делает запрос к bybit api
        по указанным в ней api_key и api_secret.
        2) Предлагает ввести тиккер валютной пары, для которой мы хотим запросить данные, например:
        BTCUSDT, ETHUSDT, DOGEUSDT ...
        для того чтобы увидеть весь список доступных валютных пар воспользуйтесь функцией:
        bybit_data.print_tickers()
        3) Предлагает ввести базовый интервал для запроса к Bybit, так же показывает все возможные
        варианты БАЗОВОГО интервала.
        4) Предлагает ввести начальную и конечную даты для запроса, обратите внимание, что запрос
        имеет смысл делать на даты не раньше 08.2021. До этого данные в API не сохранялись.
        5) Программа выводит имена получившихся DataFrame'ов, они сохранены в формате .csv
        в папке программы. Имя выглядит так:
        БАЗОВЫЙ интервал _ тиккер _ дата начала _ дата окончания
    II) bybit_data.making_datasests_X_and_Y_from_DataFrames()
    Что она делает?
        1) Предлагает ввести через пробел названия уже готовых DataFrame'ов, лежащих в папке программы
        на основе которых будут сформированы DataSet'ы X и Y. Можно ввести 1 или больше.
        2) Программа спрашивает у пользователя уровень take_profit, он же Dtp(в процентах)
        3) Программа спрашивает у пользователя уровень stop_loss, он же Dsl(в процентах)
        4) Программа спрашивает размеры свечей для DataSet X. Назовём это ПРАВИЛОМ DataSet'a
        МИНИМАЛЬНЫЙ ИНТЕРВАЛ СВЕЧИ НЕ МЕНЬШЕ БАЗОВОГО ИНТЕРВАЛА!
        Выглядит это так: 5x12 10x40 720x3 Dx2 в этом интервале DataSet X будет включать себя:
            a) 12 свечей БАЗОВОГО интервала с отступлением по 5 минут назад, то есть 5, 10, 15 ... 60
            b) 40 БАЗОВЫХ свечей по 10 минут назад, то есть 10, 20 ... 400
            c) 3 БАЗОВЫЕ свечи по 720 минут назад, то есть 720, 1440, 2160
            d) 2 БАЗОВЫЕ свечи по 1 дню назад, то есть 1 день, 2 дня
        Если свечи дублируют друг друга, то программа учитывает только одну из них, например 10x1
        дублирует 5x2, учтена будет только одна свеча без дубликатов.
        5) Программа спрашивает время, за которое сделка должна быть завершена в минутах
        6) Далле программа предлагает использовать те же параметры для оставшихся DataSet'ов,
        если согласится, то больше ничего вводить не надо будет. Если отказаться, то для следующего
        DataSet всё надо будет ввести снова.
        7) Программа выводит имена успешно созданных DataSet'ов, они сохранены в формате .csv
        в папке программы. Имя выглядит так:
        БАЗОВЫЙ интервал _ тиккер _ дата начала _ дата окончания & Dtp _ Dsl _ ПРАВИЛО DataSet'a
    III) bybit_data.create_numpy_array_for_AI_learning()
    Что она делает?
        1) Предлагает ввести через пробел названия уже готовых DataSet'ов, лежащих в папке программы
        на основе которых будут сформированы numpy array X и Y, непосредственно используемые
        для обучения нейросети. Можно ввести 1 или больше.
        2) Предлагает ввести название для сохранения X и Y в формате .csv
        3) Сохраняет X и Y в файл с указанным названием. А также возвращает numpy.array X и Y
'''


import pandas as pd
import bybit_data_5
import sys
# def eprint(e):
#     print(e, file=sys.stderr)




dataframe_save_folder = 'A:\\project_CUM\\vipavshaya_kishka\\'
dataframe_open_folder = 'A:\\project_CUM\\vipavshaya_kishka\\'
dataset_save_folder = 'A:\\project_CUM\\vipavshaya_kishka\\'


# try:
#      bybit_data_5.getting_dataframe_from_bybit_and_saving_into_csv(dataframe_save_folder)
# except Exception as e:
#      print(e, file=sys.stderr)

try:
    dataframe_open_folder = input("Enter the path to the folder with Data Frames\n")
    dataset_save_folder = input("Enter the path to the folder where you want to save the dataset\n")
    bybit_data_5.making_datasests_X_and_Y_from_DataFrames(dataframe_open_folder, dataset_save_folder)
except Exception as e:
     print(e, file=sys.stderr)

# try:
#      X, Y = bybit_data.create_numpy_array_for_AI_learning()
# except Exception as e:
#      print(e, file=sys.stderr)
