# ai-agent-skills
agent-skills测试
此工程作为maven子模块，方便用以管理；
实际agent-skills是一系列文件组成，以一个文件夹模式存在即可；此模块下面的子文件夹即代表一个skill

### file-operator-skill
文件操作的skill，目前用于测试，只支持文件读取

#### 使用
1ai agent加载此skill；可通过此文件夹的zip包或直接指定路径(ai agent支持的方式)

2 在ai agent中选择skill：file-operator，或由llm自己推理选择

3 执行：读取/Users/liyu/Downloads/123.txt内容
llm会使用此skill，并运行scripts/FileOperator脚本，获取文件内容；

#### 评测
evals里面包含了评测数据集，以及评测需要用到的数据文件；
benchmark.md：描述了评测的整体工作流：包括test case，输出产物；

根据评测结果和建议，进行skill优化；

开始评测操作：

在chat ai中，prompt输入：按照benchmark.md，进行评测，即可进行评测；输出结果一般保存在：~agent workdir/skills/file-operator/evals/results

