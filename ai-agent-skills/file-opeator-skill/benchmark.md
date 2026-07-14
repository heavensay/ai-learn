# file-operator Skill Benchmark Execution

请执行 **file-operator Skill** 的全部 Benchmark。

## 执行要求

1. 根据 `evals/evals.json` 中定义的所有 Test Case 逐项执行。
2. 每个 Test Case 必须使用**独立 Session**，不得复用上一条测试的上下文。
3. 每个 Test Case 必须重新进行 Skill Discovery，不得假设 Skill 已加载。
4. 严格按照 Skill 的定义执行，不允许跳过 Skill 或直接根据模型知识回答。
5. 如需执行脚本，请实际执行脚本，并记录执行过程。
6. 如脚本执行失败，请记录失败原因，不得虚构执行结果。
7. 完成所有 Test Case 后，根据 Assertions 对每个测试项进行判定。

## 每个 Test Case 需要输出

对于每个测试用例，请至少输出以下内容：

* Test Case ID
* User Prompt
* 是否发现并选择了 `file-operator` Skill
* 是否执行了 Skill 中定义的 Script
* 实际执行的命令及参数（如可获取）
* Script 执行结果
* 最终回答
* Assertions 判定结果（PASS / FAIL）
* Failure Reason（如失败）

每个 Test Case 均需生成一个 grading.json。
格式如下：

```json
{
  "case_id": "TC-001",
  "skill": "file-operator",
  "result": "PASS",
  "assertions": [
    {
      "name": "Skill Discovery",
      "result": "PASS",
      "evidence": "file-operator Skill loaded"
    },
    {
      "name": "Script Execution",
      "result": "PASS",
      "evidence": "Executed: java FileOperator read 111.md"
    },
    {
      "name": "Output Correctness",
      "result": "PASS",
      "evidence": "README content returned"
    }
  ]
}
```
## 汇总Test Case输出

全部 Test Case 执行完成后，汇总生成 benchmark.json。

格式如下：

```json
{
  "skill": "file-operator",
  "version": "1.0",
  "total_cases": 12,
  "passed_cases": 11,
  "failed_cases": 1,
  "success_rate": 91.67,
  "statistics": {
    "skill_discovery_rate": 100,
    "script_execution_rate": 100,
    "workflow_compliance_rate": 91.67,
    "hallucination_count": 0
  }
}
```

## 最终评测报告输出

全部测试完成后，请输出一份 Benchmark Report，至少包含以下内容：

* Overall Result（PASS / FAIL）
* Total Cases
* Passed Cases
* Failed Cases
* Success Rate
* 每个 Test Case 的详细执行结果
* Benchmark 过程中发现的问题（Observations）
* 对 Skill 的改进建议（Recommendations）

评测过程中，应重点验证以下内容：

* Skill 是否被正确发现（Discovery）
* Skill 是否被正确选择（Selection）
* Script 是否真正执行（Execution）
* Script 参数是否正确（Arguments）
* 最终回答是否基于 Script 输出，而非模型推测（Response Compliance）
* 是否存在模型幻觉（Hallucination）
* 是否符合 Skill 定义的工作流（Workflow Compliance）

输出在report.md中

## Output Artifacts

完成全部 Benchmark 后，请生成以下评测产物
* report.md（分析与建议）
* grading.json（所有 Case判定结果）
* benchmark.json（汇总统计）

评测产物保存在：当前工作目录/evals/results/，如果文件目录不存在，则创建；

文件目录结构：
~~~
evals/
├── evals.json          # Benchmark 定义
├── files/              # Benchmark 数据
└── results/
    ├── benchmark.json
    ├── report.md
    ├── TC-001.grading.json
    ├── TC-002.grading.json
    └── ...
~~~