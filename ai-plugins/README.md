# ai-plugins
claude code插件机制demo

plugin是一个自包含的组件目录，用于扩展 Claude Code 的自定义功能。插件组件包括 skills、agents、hooks、MCP servers、LSP servers 和 monitors。

## echo-plugins
一个测试插件，里面包含了skill、command、agent、hook；
hook有加载进去，但是没起作用，没进一步深究；


### 使用

- 加载

在工具目录中加载你的插件
~~~
claude --plugin-dir .claude/plugins/echo-plugin
~~~

- 验证

/plugin，查看Installed，可看到插件以及插件下的组件都已经加载进去

~~~
Plugins  Discover   Installed   Marketplaces   Errors

echo-plugin @ inline
Scope: user
Version: 1.0.0
Echo plugin with hooks, agent, command and skill

Status: Enabled · Last used: today

Installed components:
● Commands: nowtime
● Agents: echo-agent
● Skills: echo-msg
● Hooks: UserPromptSubmit


❯ Disable plugin
  Add to favorites
  Back to plugin list
~~~

- 具体使用

    指定skill调用：命令选中/echo-plugin:echo-msg
 
    agent调用：prompt提示：使用echo-agent子代理，输出问候语

    command调用：输入"/"，选中echo-plugin:nowtime即可

