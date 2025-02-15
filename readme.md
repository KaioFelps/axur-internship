EASTER_EGG_URLS

# HTML Analyzer

The provided challenge is to read a HTML response from a given URL and extract the text deepest in the tree structure,
with the constraints of not using any of built-in or third-party utilities for managing the DOM or XML structures.

To solve the problem, one simple solution is to stack each tag as it's opened. Then, on a tag closing, remove the
corresponding HTML tag from the stack. This way, even unindented structures can be correctly traversed. It worth
mentioning, however, that this solution is limited to one single element per-line (be it a tag or a text node).

## Running

**1st step:** compile the program:
```bash
javac ./HtmlAnalyzer.java
```

**2nd step:** run it!
```bash
java HtmlAnalyzer "<URL>"
```

## The algorithm
The algorithm is represented by the sequence diagram below.

![Algorithm](https://i.imgur.com/jwoAuxf.png "HTML Analyzer Algorithm")

## Operation Contracts
The required operations used within the diagram are described below:

### 1. Tag::isTagLine(content: String): boolean
**Description**
Checks whether the content is an HTML tag.

**Preconditions**
- _content_ has no leading white spaces.

**Postconditions**
- _None_.

**Expected output**
The return value is `true` if the input is a HTML tag.

### 2. Tag::parse(content: String): Tag
**Description**
Instantiates a `Tag` from a given string.

**Preconditions**
- _content_ starts with "<" char;
- _content_ ends with ">" char.

**Postconditions**
- A new instance of `Tag` has been created.

**Expected output**
The return value is a new `Tag` instance.

### 3. DomTracker::stackSize(): number
**Description**
Gets the size of the instance's DOM stack.

**Preconditions**
- _None_.

**Postconditions**
- _None_.

**Expected output**
The return value contains the current number of stacked HTML tags.

### 4. DomTracker::domIsWellFormed(): boolean
**Description**
Checks if the traversed HTML tree was free of anomalies.

**Preconditions**
- The method is called at the end of the HTML traverse.

**Postconditions**
- _None_.

**Expected output**
The return value is true if every HTML tag opened have been correctly closed, in the proper order.
