### Branching scheme

**master** -> This only receives fully fledged updates. E.g. 2.0.0, 2.1.1, etc

**develop** -> This only receives merges of fully fleshed out feature branches,
e.g. feature/autoclose, feature/autosetup

**feature/<feature_name>** -> This is an example feature branch, any feature
should be contained within a feature branch. 


### Merge checklist

- [ ] Branch is named appropriately, e.g. feature/pingcommand
- [ ] Branch is based on develop
- [ ] All tests in place (if any) pass
- [ ] Any new dependencies added are adequately explained
- [ ] All changes for the final change set are documented properly in your merge
- [ ] The bot has been tested with the docker container with a newly generated config
- [ ] If the functionality is complex, screenshots are supplied 
- [ ] If the version of kutils is being upgraded, all commands have been retested


### Issue Checklist
- [ ] You have checked current issues
- [ ] You have provided an adequate description of the issue
- [ ] Current functionality is outlined clearly
- [ ] Desired functionality is outlined clearly

