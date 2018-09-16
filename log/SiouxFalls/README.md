- original_ue.xls 是记录最初进行User Equilibrium Assignment后，各个link的情况
- one_round_optimal_without_change_graph.xls 记录的是进行Optimal操作（without demand change）后的情况，根据这一次各个link的surcharge进行排序，得到surcharge最大的10个Link
- overall记录的是每一次去除这10条link中的一条时（把capacity置为非常小的数，0.1），然后执行User Equilibrium Assignment的总体情况
- 6_8.xls 记录的是将link(6,8)的capacity置为0后，执行User Equilibrium Assignment之后的具体情况，其他类似文件相同

