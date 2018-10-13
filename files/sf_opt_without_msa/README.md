# Optimal without MSA

在之前的optimal算法的基础上不考虑使用MSA，实际每次iteration得到的marginalcost都会累积，而不会像之前那样（MSA）随着迭代次数n的增加而减少影响，所以total cost的增加是很明显的。

# 控制台输出数据

实际只迭代了20次，将每次的汇总数据total volume 和 total cost 输出如下：

total volume: 886891.25,total cost: 7543202.0
total volume: 952095.4,total cost: 2.3487362E7
total volume: 891225.25,total cost: 2.5847512E7
total volume: 1029407.56,total cost: 3.1568414E7
total volume: 979474.94,total cost: 3.9562484E7
total volume: 1098727.9,total cost: 4.9001324E7
total volume: 969402.56,total cost: 5.52623E7
total volume: 1219713.9,total cost: 6.5591372E7
total volume: 1006023.1,total cost: 7.9649496E7
total volume: 1183676.4,total cost: 6.6300672E7
total volume: 1035408.25,total cost: 9.5347424E7
total volume: 1209930.8,total cost: 7.4739088E7
total volume: 1093241.2,total cost: 1.1558228E8
total volume: 1248596.6,total cost: 9.8364384E7
total volume: 1097157.9,total cost: 1.22230304E8
total volume: 1210399.0,total cost: 9.1601064E7
total volume: 1113943.4,total cost: 1.2012752E8
total volume: 1199911.4,total cost: 9.8565824E7
total volume: 1216986.0,total cost: 1.4520216E8
total volume: 1212733.4,total cost: 1.23447064E8

其中：

- total volume：所有link的volume(flow)的和
- total cost：所有link的volume*traveltime