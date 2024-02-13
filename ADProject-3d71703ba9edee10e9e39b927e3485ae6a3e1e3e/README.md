"# ADProject_new" 

# Git crash course

## How to pull request

How to share my code with my other teammates?

This guide will merge your changes in a branch (e.g. `mk`) into the `main` branch

1. Click on the `pull request` tab at the top
2. Click on `new pull request`
3. Select your source branch. It should look like `main <- mk`
4. Click on create merge request
5. Click on `files changed` to view changes that you made in your branch (that are not in master yet).
6. If all is ok, click on `merge pull request`
7. Check that your changes are in master :)

## How to pull changes from the upstream (github.com) onto your local computer (windows)

e.g. if a pull request is merged, the main branch on github.com will be different from my local computer. How do I get these changes onto my local computer?

1. Find out what branch you are on, and if you have any pending changes.
```shell
git status
```

You should see something similar to the output below (it might be shorter, or longer)

```shell
#On branch main
#Your branch is behind 'origin/main' by 4 commits, and can be fast-forwarded.
#  (use "git pull" to update your local branch)
#
#Changes not staged for commit:
#  (use "git add <file>..." to update what will be committed)
#  (use "git restore <file>..." to discard changes in working directory)
#        modified:   src/main/java/com/ad/teamnine/AdProjectApplication.java
#
#Untracked files:
#        modified:   src/main/resources/templates/UserViews/addShoppingListIngredientPage.html
#
#Untracked files:
#  (use "git add <file>..." to include in what will be committed)
#        ../blablabla
```

- "On branch main" -> you are on the `main` branch
- "Your branch is behind 'origin/main' by 4 commits, and can be fast-forwarded" ->  the github.com version of `main` has new commits merged by someone
- "Changes not staged for commit" -> There are changes to a file that is committed before, but are not staged for commit
  - e.g. user Alice has added and committed file 123.txt
  - user John has edited the same file 123.txt
  - at this point, John's changes are *not* staged for commit
  - if John does a `git add 123.txt`, John's changes will become staged for commit
  - if John does a `git commit -m "edited 123.txt"`, John's changes will be committed, but on his local computer
  - if John does a `git push -u origin/main`, John's changes will be pushed to github.com on the `main` branch
- "Untracked files" -> New files that are created
  - e.g. user Alice has created file abc.txt that did not exist before
  - then, file abc.txt is an untracked file.
  - once Alice has `git add abc.txt && git commit -m "Add abc.txt`, then abc.txt becomes a tracked file
  
2. Now that we know what branch we are on, we can do a `git pull` since it can be *fast-forwarded*

```shell
git pull
```

Usually this will succeed, but sometimes you can get the following output

```shell
error: Your local changes to the following files would be overwritten by merge:
        ADProject-3d71703ba9edee10e9e39b927e3485ae6a3e1e3e/src/main/java/com/ad/teamnine/controller/APIController.java
        ADProject-3d71703ba9edee10e9e39b927e3485ae6a3e1e3e/src/main/java/com/ad/teamnine/model/Ingredient.java        ADProject-3d71703ba9edee10e9e39b927e3485ae6a3e1e3e/src/main/java/com/ad/teamnine/model/Recipe.java
Please commit your changes or stash them before you merge.
error: The following untracked working tree files would be overwritten by merge:
        .project
Please move or remove them before you merge.
Aborting
Updating 13ba50a..ca20987
```

don't panic!

- this means that the files that you edited on your local computer may have been modified on github.com, so git prevents the pull to protect your local edits.
- you can either
  1. commit your changes by doing `git add .` and `git commit -m "some changes I want to save"` then do a `git pull` again
  2. or you can do a `git stash` to temporarily store your files somewhere else, then `git pull` then `git stash apply`. you can `git stash drop` if your changes look ok after the git stash apply.
    - note that git stash does not stash untracked files (newly created files)
    
3. Now your local main branch is synced with the github.com main branch (remote)
4. You can `git checkout <branch-name>` (e.g. git checkout mk) to go to the working branch, then do a `git branch` to check that you are on the correct branch.
5. Finally, do a `git merge master` and a `git push`. Merge any conflicts if you have.