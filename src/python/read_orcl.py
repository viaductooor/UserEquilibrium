import cx_Oracle as oracle
import pandas as pd

#get connection
conn = oracle.connect('system/Greenpeace1@localhost:1521/orcl')

#get distinct taxi ids
cursor = conn.cursor()
#cursor.execute('SELECT DISTINCT DEVICE_ID FROM TR_GPSTRACK_CZC_20140805')
#device_id = pd.Series(cursor.fetchall())
#print(device_id)
str0 = '渝B1T610'
str = 'SELECT LON,LAT,DATE_GPS,SPEED_GPS FROM TR_GPSTRACK_CZC_20140805 WHERE DEVICE_ID = \'' + str0+'\''

cursor.execute(str)
taxi1 = cursor.fetchall()
df = pd.DataFrame(taxi1)

head = [i[0] for i in cursor.description]
d = {}
for i in range(len(head)):
    d[i] = head[i]
df2 = df.rename(columns=d)
print(df2)

cursor.close()
conn.close()
