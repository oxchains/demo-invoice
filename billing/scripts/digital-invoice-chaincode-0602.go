package main

import (
	"bytes"
	"encoding/json"
        "errors"
        "fmt"
        "strconv"
	"time"
	"strings"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

type SimpleChaincode struct {
}

type bill struct {
	ObjectType	string `json:"docType"`	//docType is used to distinguish the various types of objects in state database
	DueDate		string `json:"dueDate"`	//the fieldtags are needed to keep case from bouncing around
	Price		int `json:"price"`		//1
	CreatorName	string `json:"creatorName"`	//2
	PayerName	string `json:"payerName"`	//3
	PayeeName	string `json:"payeeName"`	//4
	ForbidTransfer	string `json:"forbidTransfer"`	//5
	CreateTime	string `json:"createTime"`	//6
	Phase		string `json:"phase"`		//8
	PayerState	string `json:"payerState"`	//a
	PayeeState	string `json:"payeeState"`	//b
	WarrantorCount	int `json:"warrantorCount"`	//c
	WarrantorState	string `json:"warrantorState"`	//d
	WarrantorName	string `json:"warrantorName"`	//e
	BillState	string `json:"billState"`	//f
	OwnerName	string `json:"ownerName"`	//g
	InvokeTarget	string `json:"invokeTarget"`	//h
	TransferState	string `json:"transferState"`	//i
	InvokeOperation	string `json:"invokeOperation"`	//o
	FinishState	string `json:"finishState"`	//p
}

type billKV struct {
	Key	string `json:"Key"`
	Record	bill `json:"Record"`
}

func (t *SimpleChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	_, args := stub.GetFunctionAndParameters()
        var A, B string    // Entities
        var Aval, Bval int // Asset holdings
        var err error

        if len(args) != 4 {
                return shim.Error("Incorrect number of arguments. Expecting 4")
        }

        // Initialize the chaincode
        A = args[0]
        Aval, err = strconv.Atoi(args[1])
        if err != nil {
                return shim.Error("Expecting integer value for asset holding")
        }
        B = args[2]
        Bval, err = strconv.Atoi(args[3])
        if err != nil {
                return shim.Error("Expecting integer value for asset holding")
        }
        fmt.Printf("Aval = %d, Bval = %d\n", Aval, Bval)

        // Write the state to the ledger
	// Automatically registers 2 users. If needs more, invoke "register".
        err = stub.PutState("Use0" + A, []byte("1")) // User ID
        if err != nil {
                return shim.Error(err.Error())
        }
        //err = stub.PutState("Use1" + A, []byte("0"))
        //if err != nil {
        //        return shim.Error(err.Error())
        //}
        err = stub.PutState("Use2" + A, []byte(strconv.Itoa(Aval))) // How much the user account has.
        if err != nil {
                return shim.Error(err.Error())
        }
        err = stub.PutState("Use0" + B, []byte("2"))
        if err != nil {
                return shim.Error(err.Error())
        }
        //err = stub.PutState("Use1" + B, []byte("0"))
        //if err != nil {
        //        return shim.Error(err.Error())
        //}
        err = stub.PutState("Use2" + B, []byte(strconv.Itoa(Bval)))
        if err != nil {
                return shim.Error(err.Error())
        }
        err = stub.PutState("GlobBilc", []byte("0")) // Global Bill Count
        if err != nil {
                return shim.Error(err.Error())
        }
        err = stub.PutState("GlobUsec", []byte("2")) // Global User Count
        if err != nil {
                return shim.Error(err.Error())
        }
        err = stub.PutState("GlobOpSc", []byte("0")) // Global Operation Starting(prompt) Count
        if err != nil {
                return shim.Error(err.Error())
        }
        err = stub.PutState("GlobOpEc", []byte("0")) // Global Operation Ending(accept or cancel) Count
        if err != nil {
                return shim.Error(err.Error())
        }
        return shim.Success(nil)
}

func (t *SimpleChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {

	function, args := stub.GetFunctionAndParameters()

	dueNum, dueBills, err := t.checkDue(stub)

	// Check if any bill is due. If there is, "checkdue" must be invoked, and any other invoke will fail.
	if err != nil {
		return shim.Error("Error in checking due: " + err.Error())
	}

	if dueNum > 0 {
		if function != "checkdue" {
			return shim.Error(string(dueNum) + " bill(s) were due. Try again later.")
		} else {
			return shim.Success(dueBills)
		}
	}
	fmt.Printf("%d bill(s) due\n", dueNum)

	if function == "checkdue" {
		return shim.Success([]byte(strconv.Itoa(dueNum)))
	}

        if function == "delete" {
                // Deletes an entity from its state
                return t.delete(stub, args)
        }
        if function == "register" {
                // Register a new user
                return t.register(stub, args)
        }
        if function == "getuserid" {
                res, err := t.getuserid(stub, args[0])
		if err != nil {
			return shim.Error(err.Error())
		} else {
			return shim.Success(res)
		}
        }
        if function == "billregister" {
                return t.billregister(stub, args)
        }
        if function == "promptacceptance" {
                return t.promptacceptance(stub, args)
        }
        if function == "promptwarrant" {
                return t.promptwarrant(stub, args)
        }
        if function == "promptrevoke" {
                return t.promptrevoke(stub, args)
        }
        if function == "promptreceive" {
                return t.promptreceive(stub, args)
        }
        if function == "promptendorsement" {
                return t.promptendorsement(stub, args)
        }
        if function == "promptpledge" {
                return t.promptpledge(stub, args)
        }
        if function == "promptpledgerelease" {
                return t.promptpledgerelease(stub, args)
        }
        if function == "promptdiscount" {
                return t.promptdiscount(stub, args)
        }
        if function == "promptpayment" {
                return t.promptpayment(stub, args)
        }
        if function == "promptrecourse" {
                return t.promptrecourse(stub, args)
        }
	if function == "queryacceptance" {
		return t.queryacceptance(stub, args)
	}
	if function == "querywarrant" {
		return t.querywarrant(stub, args)
	}
	if function == "queryrevoke" {
		return t.queryrevoke(stub, args)
	}
	if function == "queryreceive" {
		return t.queryreceive(stub, args)
	}
	if function == "queryendorsement" {
		return t.queryendorsement(stub, args)
	}
	if function == "querypledge" {
		return t.querypledge(stub, args)
	}
	if function == "querypledgerelease" {
		return t.querypledgerelease(stub, args)
	}
	if function == "querydiscount" {
		return t.querydiscount(stub, args)
	}
	if function == "querypayment" {
		return t.querypayment(stub, args)
	}
	if function == "queryrecourse" {
		return t.queryrecourse(stub, args)
	}
        if function == "query" {
                return t.query(stub, args)
        }
        return shim.Error("Function not found.")
}

func (t *SimpleChaincode) getuserid(stub shim.ChaincodeStubInterface, args string) ([]byte, error) {
// This function returns id for username. Used on check if the user has already registered.
        var A string // Entities
        var err error

        A = "Use0" + args

        // Get the state from the ledger
        Avalbytes, err := stub.GetState(A)
        if err != nil {
                jsonResp := "{\"Error\":\"Failed to get userid for " + args + "\"}"
                return nil, errors.New(jsonResp)
        }

        if Avalbytes == nil {
                jsonResp := "{\"Error\":\"Nil id for " + args + "\"}"
                return nil, errors.New(jsonResp)
        }

        return Avalbytes, nil
}

func (t *SimpleChaincode) getbillstate(stub shim.ChaincodeStubInterface, staterange string, args string) ([]byte, error) {
// This function fetches a specific value of a bill in 0.6 and is deprecated in 1.0.
        var A string // Entities
        var err error

        A = "Bil" + staterange + args

        // Get the state from the ledger
        Avalbytes, err := stub.GetState(A)
        if err != nil {
                jsonResp := "{\"Error\":\"Failed to get billstate for " + args + "\"}"
                return nil, errors.New(jsonResp)
        }

        if Avalbytes == nil {
                jsonResp := "{\"Error\":\"Nil id for " + args + "\"}"
                return nil, errors.New(jsonResp)
        }

        return Avalbytes, nil
}

func (t *SimpleChaincode) getinvolvestate(stub shim.ChaincodeStubInterface, staterange string, args string) ([]byte, error) {
// This function maintains essential information for search in 0.6 and is deprecated in 1.0.
        var A string // Entities
        var err error

        A = "UsI" + staterange + args

        // Get the state from the ledger
        Avalbytes, err := stub.GetState(A)
        if err != nil {
                jsonResp := "{\"Error\":\"Failed to get involvestate for " + args + "\"}"
                return nil, errors.New(jsonResp)
        }

        return Avalbytes, nil
}

func (t *SimpleChaincode) getbillstatewarrant(stub shim.ChaincodeStubInterface, warrantor string, bill bill) (int, error) {
// This function checks if someone is warrantor of the bill.
// -2 : The bill has no warrantor.
// -1 : The bill has warrantor, but this user is not included.
// non-negative : This user is warrantor[return value] of the bill.
        var WarrantorCount int
	var RecordedList []string

	WarrantorCount = bill.WarrantorCount
		
		if WarrantorCount != 0 {
			Recorded := bill.WarrantorName
			RecordedList = strings.Split(string(Recorded), "|")
			if len(RecordedList) != WarrantorCount {
					return -3, errors.New("Unconsistency: size of warrantor is " + string(len(RecordedList)) + ", which expected " + strconv.Itoa(WarrantorCount))
			}
			for i := 0; i < WarrantorCount; i++ {
				if strings.Compare(RecordedList[i], warrantor) == 0 {
					return i, nil
				}
			}
		} else {
			return -2, nil
		}
		return -1, nil
		
}

func (t *SimpleChaincode) givewarrant(stub shim.ChaincodeStubInterface, bill bill) (bill, error) {
// This function shall be used whenever bill owner is changed, in order to make original owner give warrant to the bill.
	if bill.OwnerName == bill.PayerName {
		return bill, nil
	}
	Promptstate, err := t.getbillstatewarrant(stub, bill.OwnerName, bill)
	if err != nil {
		return bill, err
	}
	// WarrantorsState create
	if Promptstate == -1 {
		//bill.WarrantorState = bill.WarrantorState + "1"
		// Warrantors name append
		bill.WarrantorName = bill.WarrantorName + "|" + bill.OwnerName
		bill.WarrantorCount++
	} else if Promptstate == -2 {
		//bill.WarrantorState = "1"
		bill.WarrantorName = bill.OwnerName
		bill.WarrantorCount++
	}
	return bill, nil
}

func (t *SimpleChaincode) billregister(stub shim.ChaincodeStubInterface, args []string) pb.Response {
		var Price, BillCount int
        var err error

        if len(args) != 6 {
                return shim.Error("Incorrect number of arguments. Expecting 6")
        }
		
        Duedate, err := time.Parse("2006-01-02 15:04:05", args[1])
        if err != nil {
                return shim.Error("Due date error.")
        }
        fmt.Printf("Due date = %d (%s)\n", Duedate, args[1])

	Price, err = strconv.Atoi(args[2])
        if err != nil {
                return shim.Error("Expecting integer value for asset holding")
        }
        fmt.Printf("Price = %s\n", args[2])
		
	Userid, err := t.getuserid(stub, args[0])
        if err != nil {
                return shim.Error("Bad User name")
        }
        fmt.Printf("User = %s (%s)\n", args[0], string(Userid))
		
	Payer, err := t.getuserid(stub, args[3])
        if err != nil {
                return shim.Error("Bad Payer name")
        }
        fmt.Printf("Payer = %s (%s)\n", args[3], string(Payer))
		
	Payee, err := t.getuserid(stub, args[4])
        if err != nil {
                return shim.Error("Bad Payee name")
        }
        fmt.Printf("Payee = %s (%s)\n", args[4], string(Payee))
		
	if strings.Compare(args[0], args[4]) == 0 {
			return shim.Error("User can't be as same as Payee")
	}
	if strings.Compare(args[3], args[4]) == 0 {
			return shim.Error("Payer can't be as same as Payee")
	}
        // Write the state to the ledger
		
	// BillCount ++
	BillCountBytes, err := stub.GetState("GlobBilc")
        if err != nil {
                jsonResp := "{\"Error\":\"Failed to get Global BillCount " + "\"}"
                return shim.Error(jsonResp)
        }
	BillCount, _ = strconv.Atoi(string(BillCountBytes))
	BillCount = BillCount + 1
	BillCountString := strconv.Itoa(BillCount)
	err = stub.PutState("GlobBilc", []byte(BillCountString))
        if err != nil {
                return shim.Error(err.Error())
        }
	
	bill := bill{}
	bill.ObjectType = "bill"
	bill.DueDate = Duedate.Format("2006-01-02 15:04:05")
	bill.Price = Price
	bill.CreatorName = args[0]
	bill.PayerName = args[3]
	bill.PayeeName = args[4]
       	CanTransfer := len([]rune(args[5]))
	if CanTransfer > 0 {
		bill.ForbidTransfer = "1"
	} else {
		bill.ForbidTransfer = "0"
	}
	bill.CreateTime = time.Now().Format("2006-01-02 15:04:05")
	bill.Phase = "0"
	bill.PayerState = "0"
	bill.PayeeState = "0"
	bill.WarrantorCount = 0

	
	//err = t.insertInvolve(stub, "4", args[4], BillCountString)
        //if err != nil {
        //        return shim.Error(err.Error())
        //}
	billJSONasBytes, err := json.Marshal(bill)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState("BillStruct" + BillCountString, []byte(billJSONasBytes))
        if err != nil {
                return shim.Error(err.Error())
        }
        return shim.Success([]byte(BillCountString))
}

func (t *SimpleChaincode) getDiscountPrice(stub shim.ChaincodeStubInterface, arg string) (int, error) {
// This function check operation history to find price in discount operation.
	price, err := stub.GetState("OpS5" + arg)
        if err != nil {
                return -1, err
        }
	priceString := string(price)
	priceList := strings.Split(priceString, "|")
	if len(priceList) < 2 {
		return -1, errors.New("Bad discount information length.")
	}
	res, err := strconv.Atoi(priceList[2])
        if err != nil {
                return -1, errors.New("Expecting integer value for price.")
        }
	return res, nil
}

func (t *SimpleChaincode) modifyAccount(stub shim.ChaincodeStubInterface, username string, delta int) error {
// This function changes value in account username by delta, the result must be non-negative.
        userbytes, err := stub.GetState("Use2" + username)
        if err != nil {
                return errors.New("Failed to get state" + username)
        }
        if userbytes == nil {
                return errors.New("Entity not found")
        }
        userVal, _ := strconv.Atoi(string(userbytes))
	userVal = userVal + delta
        if userVal < 0 {
		return errors.New("user " + username + " cannot afford this.")
	}

        // Write the state back to the ledger
        err = stub.PutState("Use2" + username, []byte(strconv.Itoa(userVal)))
        if err != nil {
                return err
        }
	return nil
}

func (t *SimpleChaincode) insertOp(stub shim.ChaincodeStubInterface, optype string, args []string, bill bill) pb.Response {
// This function rewrites bill in 1.0.
	var OpSCount, l int
	var err error

	l = len(args)
        if l < 4 {
                return shim.Error("Incorrect number of arguments. Expecting 4 or more")
        }
	if strings.Compare("OpS", optype) != 0 && strings.Compare("OpE", optype) != 0 {
	        return shim.Error("Bad optype. Expecting OpS or OpE")
	}
	
	// OpSCount ++
	OpSCountBytes, err := stub.GetState("Glob" + optype + "c")
        if err != nil {
                jsonResp := "{\"Error\":\"Failed to get Global OperationCount " + "\"}"
                return shim.Error(jsonResp)
        }
	OpSCount, _ = strconv.Atoi(string(OpSCountBytes))
	OpSCount = OpSCount + 1
	OpSCountString := strconv.Itoa(OpSCount)
	err = stub.PutState("Glob" + optype + "c", []byte(OpSCountString))
        if err != nil {
                return shim.Error(err.Error())
        }
		
	err = stub.PutState(optype + "0" + OpSCountString, []byte(time.Now().Format("2006-01-02 15:04:05")))
        if err != nil {
                return shim.Error(err.Error())
        }
	err = stub.PutState(optype + "1" + OpSCountString, []byte(args[0]))
        if err != nil {
                return shim.Error(err.Error())
        }
	err = stub.PutState(optype + "2" + OpSCountString, []byte(args[1]))
        if err != nil {
                return shim.Error(err.Error())
        }
	err = stub.PutState(optype + "3" + OpSCountString, []byte(args[2]))
        if err != nil {
                return shim.Error(err.Error())
        }
	err = stub.PutState(optype + "4" + OpSCountString, []byte(args[3]))
        if err != nil {
                return shim.Error(err.Error())
        }
	if (l == 5) {
		err = stub.PutState(optype + "5" + OpSCountString, []byte(args[4]))
		if err != nil {
				return shim.Error(err.Error())
		}
	}
	// Write last involved operation into bill if the operation is exclusive
	if strings.Compare("OpS", optype) == 0 && args[3][0] >= '4' && args[3][0] <= '7' {
		bill.InvokeOperation = OpSCountString
	}
	BillNumber := args[1]
	billJSONasBytes, err := json.Marshal(bill)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState("BillStruct" + BillNumber, []byte(billJSONasBytes))
	if err != nil {
        	return shim.Error(err.Error())
	}
	return shim.Success(nil)
}

func (t *SimpleChaincode) removeInvolve(stub shim.ChaincodeStubInterface, chartype string, user string, billnumber string) (error) {
// This function maintains essential information for search in 0.6 and is deprecated in 1.0.
		Involve, err := t.getinvolvestate(stub, chartype, user)
		if err != nil {
				return errors.New("Error when fetching involve state with " + user + "," + chartype)
		}
		InvolveString := string(Involve)
		InvolveList := strings.Split(InvolveString, "|")
		InvolveLen := len(InvolveList)
		InvolveString = ""
		First := false
		for i := 0; i < InvolveLen; i++ {
			if strings.Compare(InvolveList[i], billnumber) != 0 {
				if First == false {
					First = true
				} else {
					InvolveString += "|"
				}
				InvolveString += InvolveList[i]
			}
		}
		if First == false {
			res := t.delete(stub, []string{"UsI" + chartype + user})
			if res.Status == 500 {
					return errors.New("Error in delete")
			}
		} else {
			err = stub.PutState("UsI" + chartype + user, []byte(InvolveString))
			if err != nil {
					return err
			}
		}
		return nil
}

func (t *SimpleChaincode) checkInvolve(stub shim.ChaincodeStubInterface, chartype string, user string, billnumber string) (int, error) {
// This function maintains essential information for search in 0.6 and is deprecated in 1.0.
		Involve, err := t.getinvolvestate(stub, chartype, user)
		if err != nil {
				return -1, errors.New("Error when fetching involve state with " + user + "," + chartype)
		}
		InvolveString := string(Involve)
		InvolveList := strings.Split(InvolveString, "|")
		InvolveLen := len(InvolveList)
		for i := 0; i < InvolveLen; i++ {
			if strings.Compare(InvolveList[i], billnumber) == 0 {
				return 1, nil
			}
		}
		return 0, nil
}

func (t *SimpleChaincode) insertInvolve(stub shim.ChaincodeStubInterface, chartype string, user string, billnumber string) (error) {
// This function maintains essential information for search in 0.6 and is deprecated in 1.0.
		Involve, err := t.getinvolvestate(stub, chartype, user)
		if err != nil {
				return errors.New("Error when fetching involve state with " + user + "," + chartype)
		}
		var InvolveString string
		if Involve != nil {
			InvolveString = string(Involve) + "|" + billnumber
		} else {
			InvolveString = billnumber
		}
		err = stub.PutState("UsI" + chartype + user, []byte(InvolveString))
        if err != nil {
                return err
        }
		return nil
}

func getQueryResultForQueryString(stub shim.ChaincodeStubInterface, queryString string) ([]byte, error) {

	fmt.Printf("- getQueryResultForQueryString queryString:\n%s\n", queryString)

	resultsIterator, err := stub.GetQueryResult(queryString)
	if err != nil {
		return nil, err
	}
	defer resultsIterator.Close()

	// buffer is a JSON array containing QueryRecords
	var buffer bytes.Buffer
	buffer.WriteString("[")

	bArrayMemberAlreadyWritten := false
	for resultsIterator.HasNext() {
		Key, err := resultsIterator.Next()
		if err != nil {
			return nil, err
		}
		// Add a comma before array members, suppress it for the first array member
		if bArrayMemberAlreadyWritten == true {
			buffer.WriteString(",")
		}
		buffer.WriteString("{\"Key\":")
		buffer.WriteString("\"")
		buffer.WriteString(Key.Key)
		buffer.WriteString("\"")

		buffer.WriteString(", \"Record\":")
		// Record is a JSON object, so we write as-is
		buffer.WriteString(string(Key.Value))
		buffer.WriteString("}")
		bArrayMemberAlreadyWritten = true
	}
	buffer.WriteString("]")

	fmt.Printf("- getQueryResultForQueryString queryResult:\n%s\n", buffer.String())

	return buffer.Bytes(), nil
}

func getKeysForQueryString(stub shim.ChaincodeStubInterface, queryString string) ([]string, error) {

	fmt.Printf("- getKeysForQueryString queryString:\n%s\n", queryString)

	resultsIterator, err := stub.GetQueryResult(queryString)
	if err != nil {
		return nil, err
	}
	defer resultsIterator.Close()

	// buffer is a JSON array containing QueryRecords
	var buffer []string

	for resultsIterator.HasNext() {
		Key, err := resultsIterator.Next()
		if err != nil {
			return nil, err
		}
		// Add a comma before array members, suppress it for the first array member
		buffer = append(buffer, Key.Key)
	}

	fmt.Printf("- getKeysForQueryString queryResult:\n%s\n", buffer)

	return buffer, nil
}

func (t *SimpleChaincode) checkDue(stub shim.ChaincodeStubInterface) (int, []string, error) {
        var err error

        dueTime := time.Now().Format("2006-01-02 15:04:05")

	queryString := fmt.Sprintf("{\"selector\":{\"docType\":\"bill\",\"phase\":\"1\",\"dueDate\":{\"$lt\":\"%s\"}}}", dueTime)

	queryResults, err := getKeysForQueryString(stub, queryString)
	if err != nil {
		return -1, nil, err
	}

	l := len(queryResults)
	for i := 0; i < l; i++ {
		billAsBytes, err := stub.GetState(queryResults[i])
		if err != nil {
			return -1, nil, err
		} else if billAsBytes == nil {
			return -1, errors.New("Bill does not exist")
		}
		bill := bill{}
		err = json.Unmarshal(billAsBytes, &bill) //unmarshal it aka JSON.parse()
		if err != nil {
			return -1, nil, err
		}
		bill.Phase = "2"
		bill.FinishState = "0"
		if bill.BillState == "4" || bill.BillState == "5" {
			bill, err = t.givewarrant(stub, bill)
			if err != nil {
				return -1, nil, err
			}
			bill.OwnerName = bill.InvokeTarget
		}
		billJSONasBytes, err := json.Marshal(bill)
		if err != nil {
			return -1, nil, err
		}

		fmt.Printf("updating bill %s as :%s\n", queryResults[i], bill)

		err = stub.PutState(queryResults[i], []byte(billJSONasBytes))
		if err != nil {
			return -1, nil, err
		}

	}
        return l, queryResults, nil
}

func (t *SimpleChaincode) queryacceptance(stub shim.ChaincodeStubInterface, args []string) pb.Response {
        var User string
	var res = [3]string{"","",""}
        var err error

        if len(args) != 1 {
                return shim.Error("Incorrect number of arguments. Expecting name of the person to query")
        }
		
        User = args[0]
		
	queryString := fmt.Sprintf("{\"selector\":{\"docType\":\"bill\",\"phase\":\"0\",\"payerState\":\"0\",\"payeeState\":\"0\",\"creatorName\":\"%s\"}}", User)

	queryResults, err := getQueryResultForQueryString(stub, queryString)
	if err != nil {
		return shim.Error(err.Error())
	}
	res[0] = string(queryResults)

	queryString = fmt.Sprintf("{\"selector\":{\"docType\":\"bill\",\"phase\":\"0\",\"payerState\":\"1\",\"payeeState\":\"0\",\"creatorName\":\"%s\"}}", User)

	queryResults, err = getQueryResultForQueryString(stub, queryString)
	if err != nil {
		return shim.Error(err.Error())
	}
	res[1] = string(queryResults)

	queryString = fmt.Sprintf("{\"selector\":{\"docType\":\"bill\",\"phase\":\"0\",\"payerState\":\"1\",\"payeeState\":\"0\",\"payerName\":\"%s\"}}", User)

	queryResults, err = getQueryResultForQueryString(stub, queryString)
	if err != nil {
		return shim.Error(err.Error())
	}
	res[2] = string(queryResults)

        return shim.Success([]byte(res[0] + "," + res[1] + "," + res[2]))
}

func (t *SimpleChaincode) promptacceptance(stub shim.ChaincodeStubInterface, args []string) pb.Response {
        var User, BillNumber, UserAccount string
	var l, lval int
	var err error

	l = len(args)
	if l < 2 || l > 3 {
	        return shim.Error("Incorrect number of arguments. Expecting 2 or 3")
	}
	if l == 3 {
		// lval = {1 : confirm, 0 : cancel}
		lval, err = strconv.Atoi(args[2])
		if err != nil {
				return shim.Error("Expecting integer value")
		}
		if lval != 0 && lval != 1 {
				return shim.Error("Bad confirm/cancel value")
		}
	}
	
	BillNumber = args[1]

	billAsBytes, err := stub.GetState("BillStruct" + BillNumber)
	if err != nil {
		return shim.Error("Failed to get bill:" + err.Error())
	} else if billAsBytes == nil {
		return shim.Error("Bill does not exist")
	}
	bill := bill{}
	err = json.Unmarshal(billAsBytes, &bill) //unmarshal it aka JSON.parse()
	if err != nil {
		return shim.Error(err.Error())
	}

	// Promise the bill is on stage 0
	BillStage := bill.Phase
	if (BillStage[0] != '0') {
		return shim.Error("Bad Bill Stage " + string(BillStage[0]) + ": Expected 0")
	}
	// And payee state is good
	PayState := bill.PayeeState
	if (PayState[0] != '0') {
		return shim.Error("Bad Bill payee state " + string(PayState[0]) + ": Expected 0")
	}
	Promptstate := bill.PayerState
	if l == 2 && Promptstate[0] != byte('0') {
	        return shim.Error("Bad Bill state " + string(Promptstate) + " , which expected 0")
	}
	if l == 3 && Promptstate[0] != byte('1') {
	        return shim.Error("Bad Bill state " + string(Promptstate) + " , which expected 1")
	}
	
	// User must be checked through information on the bill
	User = args[0]
	Userid, err := t.getuserid(stub, User)
        if err != nil {
                return shim.Error("Bad User name")
        }
        fmt.Printf("User = %s (%s)\n", User, string(Userid))
	if l == 2 || l == 3 && lval == 0 {
		UserAccount = bill.CreatorName
	} else if l == 3 && lval == 1 {
		UserAccount = bill.PayerName
	}
	if strings.Compare(UserAccount, User) != 0 {
	        return shim.Error("User is " + User + " , which expected " + string(UserAccount))
	}
	
	if l == 2 {
		TargetAccount := bill.PayerName
		bill.PayerState = "1"
		return t.insertOp(stub, "OpS", []string{User, BillNumber, string(TargetAccount), "0"}, bill)
	} else if l == 3 && lval == 1 {
		bill.PayerState = "9"
		return t.insertOp(stub, "OpE", []string{User, BillNumber, "1", "0"}, bill)
	} else if l == 3 && lval == 0 {
		bill.PayerState = "0"
		return t.insertOp(stub, "OpE", []string{User, BillNumber, "0", "0"}, bill)
	}
	return shim.Error("Bad arguments combination")
}

func (t *SimpleChaincode) querywarrant(stub shim.ChaincodeStubInterface, args []string) pb.Response {
        var User string
	var res = [3]string{"","",""}
        var err error

        if len(args) != 1 {
                return shim.Error("Incorrect number of arguments. Expecting name of the person to query")
        }
		
        User = args[0]
		
	queryString := fmt.Sprintf("{\"selector\":{\"docType\":\"bill\",\"phase\":\"0\",\"payeeState\":\"0\",\"creatorName\":\"%s\"}}", User)

	queryResults, err := getQueryResultForQueryString(stub, queryString)
	if err != nil {
		return shim.Error(err.Error())
	}
	res[0] = string(queryResults)
	lq := len(queryResults)
	if lq > 4 {
		firstWrite := true
		tres := strings.Split(string(queryResults[2:lq-2]), "},{")
		lres := len(tres)
		for i := 0; i < lres; i++ {
			billKV := billKV{}
			err = json.Unmarshal([]byte("{"+tres[i]+"}"), &billKV) //unmarshal it aka JSON.parse()
			if err != nil {
				return shim.Error(err.Error())
			}
			bill := billKV.Record
			fmt.Println(bill)
			if bill.WarrantorCount > 0 && strings.Contains(bill.WarrantorState, "1") {
				if firstWrite {
					firstWrite = false
				} else {
					res[1] = res[1] + ","
				}
				res[1] = res[1] + "{" + tres[i] + "}"
			}
		}
	}
	res[1] = "[" + res[1] + "]"

	queryString = fmt.Sprintf("{\"selector\":{\"docType\":\"bill\",\"phase\":\"0\",\"payeeState\":\"0\"}}")
	queryResults, err = getQueryResultForQueryString(stub, queryString)
	if err != nil {
		return shim.Error(err.Error())
	}
	lq = len(queryResults)
	if lq > 4 {
		firstWrite := true
		tres := strings.Split(string(queryResults[2:lq-2]), "},{")
		lres := len(tres)
		for i := 0; i < lres; i++ {
			billKV := billKV{}
			err = json.Unmarshal([]byte("{"+tres[i]+"}"), &billKV) //unmarshal it aka JSON.parse()
			if err != nil {
				return shim.Error(err.Error())
			}
			bill := billKV.Record
			fmt.Println(bill)
			pos, err := t.getbillstatewarrant(stub, User, bill)
			if err != nil {
				return shim.Error(err.Error())
			}
			if pos >= 0 && bill.WarrantorState[pos] == '1' {
				if firstWrite {
					firstWrite = false
				} else {
					res[2] = res[2] + ","
				}
				res[2] = res[2] + "{" + tres[i] + "}"
			}
		}
	}
	res[2] = "[" + res[2] + "]"

        return shim.Success([]byte(res[0] + "," + res[1] + "," + res[2]))
}

func (t *SimpleChaincode) promptwarrant(stub shim.ChaincodeStubInterface, args []string) pb.Response {
        var User, BillNumber, TargetName, UserAccount string
	var l, lval, Promptstate int
	var err error

	l = len(args)
        if l < 3 || l > 4 {
                return shim.Error("Incorrect number of arguments. Expecting 3 or 4")
        }
	if l == 4 {
		// lval = {1 : confirm, 0 : cancel}
		lval, err = strconv.Atoi(args[3])
		if err != nil {
				return shim.Error("Expecting integer value")
		}
		if lval != 0 && lval != 1 {
				return shim.Error("Bad confirm/cancel value")
		}
	}
	
	BillNumber = args[1]
	billAsBytes, err := stub.GetState("BillStruct" + BillNumber)
	if err != nil {
		return shim.Error("Failed to get bill:" + err.Error())
	} else if billAsBytes == nil {
		return shim.Error("Bill does not exist")
	}
	bill := bill{}
	err = json.Unmarshal(billAsBytes, &bill) //unmarshal it aka JSON.parse()
	if err != nil {
		return shim.Error(err.Error())
	}

	// Promise the bill is on stage 0
	BillStage := bill.Phase
	if (BillStage[0] != '0') {
			return shim.Error("Bad Bill Stage " + string(BillStage[0]) + ": Expected 0")
	}
	// And payee state is good
	PayState := bill.PayeeState
	if (PayState[0] != '0') {
			return shim.Error("Bad Bill payee state " + string(PayState[0]) + ": Expected 0")
	}
	
	TargetName = args[2]
	Promptstate, err = t.getbillstatewarrant(stub, TargetName, bill)
        if err != nil {
                return shim.Error("Bad Bill state of warrant")
        }
	if l == 3 && Promptstate >= 0 {
	        return shim.Error("Tried to add an existing warrantor at" + strconv.Itoa(Promptstate))
	}
	if l == 4 && Promptstate < 0 {
	        return shim.Error("Tried to comfirm or cancel an unexisting warrantor")
	}
	
	// User must be checked through information on the bill
	User = args[0]
	Userid, err := t.getuserid(stub, User)
        if err != nil {
                return shim.Error("Bad User name")
        }
        fmt.Printf("User = %s (%s)\n", User, string(Userid))
	Targetid, err := t.getuserid(stub, TargetName)
        if err != nil {
                return shim.Error("Bad Warrantor name")
        }
        fmt.Printf("Warrantor = %s (%s)\n", TargetName, string(Targetid))
		
		UserAccount = bill.PayeeName
		if strings.Compare(string(UserAccount), TargetName) == 0 {
				return shim.Error("Warrantor can't be as same as Payee")
		}
		UserAccount = bill.PayerName
		if strings.Compare(string(UserAccount), TargetName) == 0 {
				return shim.Error("Warrantor can't be as same as Payer")
		}
		UserAccount = bill.CreatorName
		if strings.Compare(string(UserAccount), TargetName) == 0 {
				return shim.Error("Warrantor can't be as same as Bill registrant")
		}
		if l == 3 || l == 4 && lval == 0 {
			if strings.Compare(string(UserAccount), User) != 0 {
					return shim.Error("User is " + User + " , which expected " + string(UserAccount))
			}
		}
		
		if l == 3 {
			// WarrantorCount ++
			bill.WarrantorCount++
			// WarrantorsState create
			if Promptstate != -2 {
				bill.WarrantorState = bill.WarrantorState + "1"
				// Warrantors name append
				bill.WarrantorName = bill.WarrantorName + "|" + TargetName
			} else {
				bill.WarrantorState = "1"
				bill.WarrantorName = TargetName
			}
			
			
			//err = t.insertInvolve(stub, "e", TargetName, BillNumber)
			//if err != nil {
			//		return shim.Error(err.Error())
			//}
			return t.insertOp(stub, "OpS", []string{User, BillNumber, TargetName, "1"}, bill)
			
		} else if l == 4 {
			// WarrantorsState
			WarrantorsState := bill.WarrantorState
			if WarrantorsState[Promptstate] == '9' {
					return shim.Error("This warrantor has already comfirmed.")
			}
			if lval == 1 {
				WBytes := []byte(WarrantorsState)
				WBytes[Promptstate] = '9'
				WarrantorsState = string(WBytes)
			} else {
				// WarrantorCount --
				bill.WarrantorCount--
				// WarrantorsState delete
				WarrantorsState = WarrantorsState[0:Promptstate] + WarrantorsState[Promptstate+1:bill.WarrantorCount+1]
				// Warrantors name delete
				WarrantorsString := bill.WarrantorName
				WarrantorsList := strings.Split(WarrantorsString, "|")
				WarrantorsString = ""
				for i := 0; i <= bill.WarrantorCount; i++ {
					if i != Promptstate {
						WarrantorsString += WarrantorsList[i]
					}
				}
				bill.WarrantorName = WarrantorsString
				
				//err = t.removeInvolve(stub, "e", TargetName, BillNumber)
				//if err != nil {
				//		return shim.Error(err.Error())
				//}
			}
			bill.WarrantorState = WarrantorsState
			if lval == 1 {
				return t.insertOp(stub, "OpE", []string{User, BillNumber, "1", "1"}, bill)
			} else {
				return t.insertOp(stub, "OpE", []string{User, BillNumber, "0", "1"}, bill)
			}
		}
		return shim.Error("Bad arguments combination")
}

func (t *SimpleChaincode) queryrevoke(stub shim.ChaincodeStubInterface, args []string) pb.Response {
        var User string
        var err error

        if len(args) != 1 {
                return shim.Error("Incorrect number of arguments. Expecting name of the person to query")
        }
		
        User = args[0]
		
	queryString := fmt.Sprintf("{\"selector\":{\"docType\":\"bill\",\"phase\":\"0\",\"payeeState\":\"0\",\"creatorName\":\"%s\"}}", User)

	queryResults, err := getQueryResultForQueryString(stub, queryString)
	if err != nil {
		return shim.Error(err.Error())
	}


        return shim.Success(queryResults)
}


func (t *SimpleChaincode) promptrevoke(stub shim.ChaincodeStubInterface, args []string) pb.Response {
        var User, BillNumber, UserAccount string
	var l int
	var err error

	l = len(args)
        if l != 2 {
                return shim.Error("Incorrect number of arguments. Expecting 2")
        }
		
	BillNumber = args[1]
	billAsBytes, err := stub.GetState("BillStruct" + BillNumber)
	if err != nil {
		return shim.Error("Failed to get bill:" + err.Error())
	} else if billAsBytes == nil {
		return shim.Error("Bill does not exist")
	}
	bill := bill{}
	err = json.Unmarshal(billAsBytes, &bill) //unmarshal it aka JSON.parse()
	if err != nil {
		return shim.Error(err.Error())
	}

	// Promise the bill is on stage 0
	BillStage := bill.Phase
	if (BillStage[0] != '0') {
			return shim.Error("Bad Bill Stage " + string(BillStage[0]) + ": Expected 0")
	}
	// And payee state is good
	PayState := bill.PayeeState
	if (PayState[0] != '0') {
			return shim.Error("Bad Bill payee state " + string(PayState[0]) + ": Expected 0")
	}
	
	// User must be checked through information on the bill
	User = args[0]
	Userid, err := t.getuserid(stub, User)
        if err != nil {
                return shim.Error("Bad User name")
        }
        fmt.Printf("User = %s (%s)\n", User, string(Userid))
	UserAccount = bill.CreatorName
	if strings.Compare(UserAccount, User) != 0 {
	        return shim.Error("User is " + User + " , which expected " + string(UserAccount))
	}
	
	bill.PayeeState = "2"
	return t.insertOp(stub, "OpS", []string{User, BillNumber, "0", "2"}, bill)
}

func (t *SimpleChaincode) queryreceive(stub shim.ChaincodeStubInterface, args []string) pb.Response {
        var User string
	var res = [3]string{"","",""}
        var err error

        if len(args) != 1 {
                return shim.Error("Incorrect number of arguments. Expecting name of the person to query")
        }
		
        User = args[0]
		
	queryString := fmt.Sprintf("{\"selector\":{\"docType\":\"bill\",\"phase\":\"0\",\"payerState\":\"9\",\"payeeState\":\"0\",\"creatorName\":\"%s\"}}", User)
	queryResults, err := getQueryResultForQueryString(stub, queryString)
	if err != nil {
		return shim.Error(err.Error())
	}
	lq := len(queryResults)
	if lq > 4 {
		firstWrite := true
		tres := strings.Split(string(queryResults[2:lq-2]), "},{")
		lres := len(tres)
		for i := 0; i < lres; i++ {
			billKV := billKV{}
			err = json.Unmarshal([]byte("{"+tres[i]+"}"), &billKV) //unmarshal it aka JSON.parse()
			if err != nil {
				return shim.Error(err.Error())
			}
			bill := billKV.Record
			fmt.Println(bill)
			if strings.Count(bill.WarrantorState, "9") == bill.WarrantorCount {
				if firstWrite {
					firstWrite = false
				} else {
					res[0] = res[0] + ","
				}
				res[0] = res[0] + "{" + tres[i] + "}"
			}
		}
	}
	res[0] = "[" + res[0] + "]"

	queryString = fmt.Sprintf("{\"selector\":{\"docType\":\"bill\",\"phase\":\"0\",\"payeeState\":\"1\",\"creatorName\":\"%s\"}}", User)

	queryResults, err = getQueryResultForQueryString(stub, queryString)
	if err != nil {
		return shim.Error(err.Error())
	}
	res[1] = string(queryResults)

	queryString = fmt.Sprintf("{\"selector\":{\"docType\":\"bill\",\"phase\":\"0\",\"payeeState\":\"1\",\"payeeName\":\"%s\"}}", User)

	queryResults, err = getQueryResultForQueryString(stub, queryString)
	if err != nil {
		return shim.Error(err.Error())
	}
	res[2] = string(queryResults)

        return shim.Success([]byte(res[0] + "," + res[1] + "," + res[2]))
}


func (t *SimpleChaincode) promptreceive(stub shim.ChaincodeStubInterface, args []string) pb.Response {
        var User, BillNumber, UserAccount string
	var l, lval int
	var err error

	l = len(args)
        if l < 2 || l > 3 {
                return shim.Error("Incorrect number of arguments. Expecting 2 or 3")
        }
	if l == 3 {
		// lval = {-1 : refuse confirm, 1 : confirm, 0 : cancel}
		lval, err = strconv.Atoi(args[2])
		if err != nil {
				return shim.Error("Expecting integer value")
		}
		if lval != 0 && lval != 1 && lval != -1 {
				return shim.Error("Bad confirm/cancel value")
		}
	}
		
	BillNumber = args[1]
	billAsBytes, err := stub.GetState("BillStruct" + BillNumber)
	if err != nil {
		return shim.Error("Failed to get bill:" + err.Error())
	} else if billAsBytes == nil {
		return shim.Error("Bill does not exist")
	}
	bill := bill{}
	err = json.Unmarshal(billAsBytes, &bill) //unmarshal it aka JSON.parse()
	if err != nil {
		return shim.Error(err.Error())
	}

	// Promise the bill is on stage 0
	BillStage := bill.Phase
	if (BillStage[0] != '0') {
			return shim.Error("Bad Bill Stage " + string(BillStage[0]) + ": Expected 0")
	}
		
	Receivestate := bill.PayeeState
	if l == 2 && Receivestate[0] != '0' {
	        return shim.Error("Bad Payee state " + Receivestate + " , which expected 0")
	}
	if l == 3 && Receivestate[0] != '1' {
	        return shim.Error("Bad Payee state " + Receivestate + " , which expected 1")
	}
	
	// User must be checked through information on the bill
	User = args[0]
	Userid, err := t.getuserid(stub, User)
        if err != nil {
                return shim.Error("Bad User name")
        }
        fmt.Printf("User = %s (%s)\n", User, string(Userid))
	if l == 2 || l == 3 && lval == 0 {
		UserAccount = bill.CreatorName
	} else if l == 3 && (lval == 1 || lval == -1) {
		UserAccount = bill.PayeeName
	}
	if strings.Compare(UserAccount, User) != 0 {
	        return shim.Error("User is " + User + " , which expected " + UserAccount)
	}
	
	if l == 2 {
		// Check payer state
		PayerState := bill.PayerState
		if (PayerState[0] != '9') {
				return shim.Error("Bad payer state " + string(PayerState[0]) + ": Expected 9")
		}	
		
		// Check warrant state
		WarrantorCount := bill.WarrantorCount
		if WarrantorCount > 0 {
			Warrantors := bill.WarrantorState
			for i := 0; i < WarrantorCount; i++ {
				if Warrantors[i] != '9' {
					return shim.Error("Warrantor number " + strconv.Itoa(i) + " has state " + string(Warrantors[i]) + ", expected 9")
				}
			}
		}
		
		TargetAccount := bill.PayeeName
		bill.PayeeState = "1"
		return t.insertOp(stub, "OpS", []string{User, BillNumber, string(TargetAccount), "3"}, bill)
	} else if l == 3 && lval == 1 {
		bill.PayeeState = "9" // 完成
		bill.Phase = "1" // 等待
		bill.BillState = "0" // 未做
		bill.OwnerName = UserAccount
		bill.TransferState = "0"
		//err = t.insertInvolve(stub, "g", string(UserAccount), BillNumber)
		//if err != nil {
		//		return shim.Error(err.Error())
		//}
		return t.insertOp(stub, "OpE", []string{User, BillNumber, "1", "3"}, bill)
	} else if l == 3 && lval == 0 {
		bill.PayeeState = "0"
		return t.insertOp(stub, "OpE", []string{User, BillNumber, "0", "3"}, bill)
	} else if l == 3 && lval == -1 {
		bill.PayeeState = "3" // 收票人废弃 2: 出票人废弃
		return t.insertOp(stub, "OpE", []string{User, BillNumber, "-1", "3"}, bill)
	}
	return shim.Error("Bad arguments combination")
}

func (t *SimpleChaincode) queryendorsement(stub shim.ChaincodeStubInterface, args []string) pb.Response {
        var User string
	var res = [3]string{"","",""}
        var err error

        if len(args) != 1 {
                return shim.Error("Incorrect number of arguments. Expecting name of the person to query")
        }
		
        User = args[0]
		
	queryString := fmt.Sprintf("{\"selector\":{\"docType\":\"bill\",\"phase\":\"1\",\"billState\":\"0\",\"forbidTransfer\":\"0\",\"transferState\":{\"$ne\":\"9\"},\"ownerName\":\"%s\"}}", User)

	queryResults, err := getQueryResultForQueryString(stub, queryString)
	if err != nil {
		return shim.Error(err.Error())
	}
	res[0] = string(queryResults)

	queryString = fmt.Sprintf("{\"selector\":{\"docType\":\"bill\",\"phase\":\"1\",\"billState\":\"1\",\"ownerName\":\"%s\"}}", User)

	queryResults, err = getQueryResultForQueryString(stub, queryString)
	if err != nil {
		return shim.Error(err.Error())
	}
	res[1] = string(queryResults)

	queryString = fmt.Sprintf("{\"selector\":{\"docType\":\"bill\",\"phase\":\"1\",\"billState\":\"1\",\"invokeTarget\":\"%s\"}}", User)

	queryResults, err = getQueryResultForQueryString(stub, queryString)
	if err != nil {
		return shim.Error(err.Error())
	}
	res[2] = string(queryResults)

        return shim.Success([]byte(res[0] + "," + res[1] + "," + res[2]))
}

func (t *SimpleChaincode) promptendorsement(stub shim.ChaincodeStubInterface, args []string) pb.Response {
        var User, BillNumber, TargetName, UserAccount string
	var l, lval int
	var err error

	l = len(args)
        if l < 3 || l > 4 {
                return shim.Error("Incorrect number of arguments. Expecting 3 or 4")
        }
	if l == 4 {
		// lval = {1 : confirm, 0 : cancel, -1 : cannot transfer}
		lval, err = strconv.Atoi(args[3])
		if err != nil {
				return shim.Error("Expecting integer value")
		}
		if lval != 0 && lval != 1 && lval != -1 {
				return shim.Error("Bad confirm/cancel value")
		}
	}
	if (l == 3 || lval == -1) && strings.Compare(args[0], args[2]) == 0 {
			return shim.Error("User can't be as same as Target")
	}
	
	BillNumber = args[1]
	billAsBytes, err := stub.GetState("BillStruct" + BillNumber)
	if err != nil {
		return shim.Error("Failed to get bill:" + err.Error())
	} else if billAsBytes == nil {
		return shim.Error("Bill does not exist")
	}
	bill := bill{}
	err = json.Unmarshal(billAsBytes, &bill) //unmarshal it aka JSON.parse()
	if err != nil {
		return shim.Error(err.Error())
	}

	// Promise the bill is on stage 1
	BillStage := bill.Phase
	if (BillStage[0] != '1') {
			return shim.Error("Bad Bill Stage " + string(BillStage[0]) + ": Expected 1")
	}
	// And transfer is available
	TransferState := bill.ForbidTransfer
	if TransferState[0] == '1' {
			return shim.Error("The bill is forbidden to transfer")
	}
	TransferState = bill.TransferState
	if TransferState[0] == '9' {
			return shim.Error("The bill is forbidden to transfer")
	}
	// And circulation state is good
	CirculationState := bill.BillState
	if (l == 3 || lval == -1) && CirculationState[0] != '0' {
	        return shim.Error("Bad circulation state " + CirculationState + " , which expected 0")
	}
	if l == 4 && (lval == 0 || lval == 1) && CirculationState[0] != '1' {
	        return shim.Error("Bad circulation state " + CirculationState + " , which expected 1")
	}
	
	// User must be checked through information on the bill
	User = args[0]
	Userid, err := t.getuserid(stub, User)
        if err != nil {
                return shim.Error("Bad User name")
        }
        fmt.Printf("User = %s (%s)\n", User, string(Userid))
	TargetName = args[2]
	Userid, err = t.getuserid(stub, TargetName)
        if err != nil {
                return shim.Error("Bad target name")
        }
	if l == 3 || lval == -1 || lval == 0 {
		UserAccount = bill.OwnerName
	} else if l == 4 && lval == 1 {
		UserAccount = bill.InvokeTarget
	}
	if strings.Compare(UserAccount, User) != 0 {
	        return shim.Error("User is " + User + " , which expected " + UserAccount)
	}
		
	if l == 3 || lval == -1 {
		bill.BillState = "1"
		bill.InvokeTarget = TargetName
		//err = t.insertInvolve(stub, "ha", TargetName, BillNumber)
		//if err != nil {
		//		return shim.Error(err.Error())
		//}
		if l == 3 {
			return t.insertOp(stub, "OpS", []string{User, BillNumber, TargetName, "4"}, bill)
		} else {
			bill.TransferState = "1"
			return t.insertOp(stub, "OpS", []string{User, BillNumber, TargetName, "4", "-1"}, bill)
		}
	} else if l == 4 && lval == 1 {
		bill.BillState = "0"
		//err = t.removeInvolve(stub, "ha", User, BillNumber)
		//if err != nil {
		//		return shim.Error(err.Error())
		//}
		//LastOwner := bill.OwnerName
		//err = t.removeInvolve(stub, "g", LastOwner, BillNumber)
		//if err != nil {
		//		return shim.Error(err.Error())
		//}
		//err = t.insertInvolve(stub, "g", User, BillNumber)
		//if err != nil {
		//		return shim.Error(err.Error())
		//}
		bill, err = t.givewarrant(stub, bill)
		if err != nil {
			return shim.Error(err.Error())
		}
		bill.OwnerName = User
		if TransferState[0] == '1' {
			bill.TransferState = "9"
		}
		return t.insertOp(stub, "OpE", []string{User, BillNumber, "1", "4"}, bill)
	} else if l == 4 && lval == 0 {
		bill.BillState = "0"
		//LastTarget := bill.InvokeTarget
		//err = t.removeInvolve(stub, "ha", string(LastTarget), BillNumber)
		//if err != nil {
		//		return shim.Error(err.Error())
		//}
		if TransferState[0] == '1' {
			bill.TransferState = "0"
		}
		return t.insertOp(stub, "OpE", []string{User, BillNumber, "0", "4"}, bill)
	}
	return shim.Error("Bad arguments combination")
}

func (t *SimpleChaincode) querypledge(stub shim.ChaincodeStubInterface, args []string) pb.Response {
        var User string
	var res = [3]string{"","",""}
        var err error

        if len(args) != 1 {
                return shim.Error("Incorrect number of arguments. Expecting name of the person to query")
        }
		
        User = args[0]
		
	queryString := fmt.Sprintf("{\"selector\":{\"docType\":\"bill\",\"phase\":\"1\",\"billState\":\"0\",\"forbidTransfer\":\"0\",\"transferState\":{\"$ne\":\"9\"},\"ownerName\":\"%s\"}}", User)

	queryResults, err := getQueryResultForQueryString(stub, queryString)
	if err != nil {
		return shim.Error(err.Error())
	}
	res[0] = string(queryResults)

	queryString = fmt.Sprintf("{\"selector\":{\"docType\":\"bill\",\"phase\":\"1\",\"billState\":\"3\",\"ownerName\":\"%s\"}}", User)

	queryResults, err = getQueryResultForQueryString(stub, queryString)
	if err != nil {
		return shim.Error(err.Error())
	}
	res[1] = string(queryResults)

	queryString = fmt.Sprintf("{\"selector\":{\"docType\":\"bill\",\"phase\":\"1\",\"billState\":\"3\",\"invokeTarget\":\"%s\"}}", User)

	queryResults, err = getQueryResultForQueryString(stub, queryString)
	if err != nil {
		return shim.Error(err.Error())
	}
	res[2] = string(queryResults)

        return shim.Success([]byte(res[0] + "," + res[1] + "," + res[2]))
}


func (t *SimpleChaincode) promptpledge(stub shim.ChaincodeStubInterface, args []string) pb.Response {
        var User, BillNumber, TargetName, UserAccount string
	var l, lval int
	var err error

	l = len(args)
        if l < 3 || l > 4 {
                return shim.Error("Incorrect number of arguments. Expecting 3 or 4")
        }
	if l == 4 {
		// lval = {1 : confirm, 0 : cancel}
		lval, err = strconv.Atoi(args[3])
		if err != nil {
				return shim.Error("Expecting integer value")
		}
		if lval != 0 && lval != 1 {
				return shim.Error("Bad confirm/cancel value")
		}
	}
	if l == 3 && strings.Compare(args[0], args[2]) == 0 {
			return shim.Error("User can't be as same as Target")
	}
	
	BillNumber = args[1]
	// Promise the bill is on stage 1
	billAsBytes, err := stub.GetState("BillStruct" + BillNumber)
	if err != nil {
		return shim.Error("Failed to get bill:" + err.Error())
	} else if billAsBytes == nil {
		return shim.Error("Bill does not exist")
	}
	bill := bill{}
	err = json.Unmarshal(billAsBytes, &bill) //unmarshal it aka JSON.parse()
	if err != nil {
		return shim.Error(err.Error())
	}
	BillStage := bill.Phase
	if (BillStage[0] != '1') {
			return shim.Error("Bad Bill Stage " + string(BillStage[0]) + ": Expected 1")
	}
	// And transfer is available
	TransferState := bill.ForbidTransfer
	if TransferState[0] == '1' {
		return shim.Error("The bill is forbidden to transfer")
	}
	TransferState = bill.TransferState
	if TransferState[0] == '9' {
			return shim.Error("The bill is forbidden to transfer")
	}
	// And circulation state is good
	CirculationState := bill.BillState
	if l == 3 && CirculationState[0] != '0' {
	        return shim.Error("Bad circulation state " + CirculationState + " , which expected 0")
	}
	if l == 4 && CirculationState[0] != '3' {
	        return shim.Error("Bad circulation state " + CirculationState + " , which expected 3")
	}
	
	// User must be checked through information on the bill
	User = args[0]
	Userid, err := t.getuserid(stub, User)
        if err != nil {
                return shim.Error("Bad User name")
        }
        fmt.Printf("User = %s (%s)\n", User, string(Userid))
	TargetName = args[2]
	Userid, err = t.getuserid(stub, TargetName)
        if err != nil {
                return shim.Error("Bad target name")
        }
	if l == 3 || lval == 0 {
		UserAccount= bill.OwnerName
	} else if l == 4 && lval == 1 {
		UserAccount = bill.InvokeTarget
	}
	if strings.Compare(UserAccount, User) != 0 {
	        return shim.Error("User is " + User + " , which expected " + string(UserAccount))
	}
	
	if l == 3 {
		bill.BillState = "3" // 提示质押
		bill.InvokeTarget = TargetName
		//err = t.insertInvolve(stub, "hc", TargetName, BillNumber)
		//if err != nil {
		//		return shim.Error(err.Error())
		//}
		return t.insertOp(stub, "OpS", []string{User, BillNumber, TargetName, "6"}, bill)
	} else if l == 4 && lval == 1 {
		bill.BillState = "4" // 已质押
		return t.insertOp(stub, "OpE", []string{User, BillNumber, "1", "6"}, bill)
	} else if l == 4 && lval == 0 {
		bill.BillState = "0" // 正常流通
		//LastTarget = bill.InvokeTarget
		//err = t.removeInvolve(stub, "hc", string(LastTarget), BillNumber)
		//if err != nil {
		//		return shim.Error(err.Error())
		//}
		return t.insertOp(stub, "OpE", []string{User, BillNumber, "0", "6"}, bill)
	}
	return shim.Error("Bad arguments combination")
}

func (t *SimpleChaincode) querypledgerelease(stub shim.ChaincodeStubInterface, args []string) pb.Response {
        var User string
	var res = [3]string{"","",""}
        var err error

        if len(args) != 1 {
                return shim.Error("Incorrect number of arguments. Expecting name of the person to query")
        }
		
        User = args[0]
		
	queryString := fmt.Sprintf("{\"selector\":{\"docType\":\"bill\",\"phase\":\"1\",\"billState\":\"4\",\"invokeTarget\":\"%s\"}}", User)

	queryResults, err := getQueryResultForQueryString(stub, queryString)
	if err != nil {
		return shim.Error(err.Error())
	}
	res[0] = string(queryResults)

	// 5: 质押解除
	queryString = fmt.Sprintf("{\"selector\":{\"docType\":\"bill\",\"phase\":\"1\",\"billState\":\"5\",\"invokeTarget\":\"%s\"}}", User)

	queryResults, err = getQueryResultForQueryString(stub, queryString)
	if err != nil {
		return shim.Error(err.Error())
	}
	res[1] = string(queryResults)

	queryString = fmt.Sprintf("{\"selector\":{\"docType\":\"bill\",\"phase\":\"1\",\"billState\":\"5\",\"ownerName\":\"%s\"}}", User)

	queryResults, err = getQueryResultForQueryString(stub, queryString)
	if err != nil {
		return shim.Error(err.Error())
	}
	res[2] = string(queryResults)

        return shim.Success([]byte(res[0] + "," + res[1] + "," + res[2]))
}

func (t *SimpleChaincode) promptpledgerelease(stub shim.ChaincodeStubInterface, args []string) pb.Response {
        var User, BillNumber, UserAccount string
	var l, lval int
	var err error

	l = len(args)
        if l < 2 || l > 3 {
                return shim.Error("Incorrect number of arguments. Expecting 2 or 3")
        }
	if l == 3 {
		// lval = {1 : confirm, 0 : cancel}
		lval, err = strconv.Atoi(args[2])
		if err != nil {
				return shim.Error("Expecting integer value")
		}
		if lval != 0 && lval != 1 {
				return shim.Error("Bad confirm/cancel value")
		}
	}

	BillNumber = args[1]
	billAsBytes, err := stub.GetState("BillStruct" + BillNumber)
	if err != nil {
		return shim.Error("Failed to get bill:" + err.Error())
	} else if billAsBytes == nil {
		return shim.Error("Bill does not exist")
	}
	bill := bill{}
	err = json.Unmarshal(billAsBytes, &bill) //unmarshal it aka JSON.parse()
	if err != nil {
		return shim.Error(err.Error())
	}	
	// Promise the bill is on stage 1

	BillStage := bill.Phase
	if (BillStage[0] != '1') {
			return shim.Error("Bad Bill Stage " + string(BillStage[0]) + ": Expected 1")
	}
	// And transfer is available
	TransferState := bill.ForbidTransfer
	if TransferState[0] == '1' {
			return shim.Error("The bill is forbidden to transfer")
	}
	TransferState = bill.TransferState
	if TransferState[0] == '9' {
			return shim.Error("The bill is forbidden to transfer")
	}
	// And circulation state is good
	CirculationState := bill.BillState
	if l == 2 && CirculationState[0] != '4' {
	        return shim.Error("Bad circulation state " + CirculationState + " , which expected 4")
	}
	if l == 3 && CirculationState[0] != '5' {
	        return shim.Error("Bad circulation state " + CirculationState + " , which expected 5")
	}
	
	// User must be checked through information on the bill
	User = args[0]
	Userid, err := t.getuserid(stub, User)
        if err != nil {
                return shim.Error("Bad User name")
        }
        fmt.Printf("User = %s (%s)\n", User, string(Userid))
	if l == 2 || lval == 0 {
		UserAccount = bill.InvokeTarget
	} else if l == 3 && lval == 1 {
		UserAccount = bill.OwnerName
	}
	if strings.Compare(UserAccount, User) != 0 {
	        return shim.Error("User is " + User + " , which expected " + string(UserAccount))
	}
	
	if l == 2 {
		bill.BillState = "5"
		return t.insertOp(stub, "OpS", []string{User, BillNumber, "-1", "7"}, bill)
	} else if l == 3 && lval == 1 {
		//Pledger = bill.InvokeTarget
		bill.BillState = "0"
		//err = t.removeInvolve(stub, "hc", string(Pledger), BillNumber)
		//if err != nil {
		//		return shim.Error(err.Error())
		//}
		return t.insertOp(stub, "OpE", []string{User, BillNumber, "1", "7"}, bill)
	} else if l == 3 && lval == 0 {
		bill.BillState = "4"
		return t.insertOp(stub, "OpE", []string{User, BillNumber, "0", "7"}, bill)
	}
	return shim.Error("Bad arguments combination")
}

func (t *SimpleChaincode) querydiscount(stub shim.ChaincodeStubInterface, args []string) pb.Response {
        var User string
	var res = [3]string{"","",""}
        var err error

        if len(args) != 1 {
                return shim.Error("Incorrect number of arguments. Expecting name of the person to query")
        }
		
        User = args[0]
		
	queryString := fmt.Sprintf("{\"selector\":{\"docType\":\"bill\",\"phase\":\"1\",\"billState\":\"0\",\"forbidTransfer\":\"0\",\"transferState\":{\"$ne\":\"9\"},\"ownerName\":\"%s\"}}", User)

	queryResults, err := getQueryResultForQueryString(stub, queryString)
	if err != nil {
		return shim.Error(err.Error())
	}
	res[0] = string(queryResults)

	queryString = fmt.Sprintf("{\"selector\":{\"docType\":\"bill\",\"phase\":\"1\",\"billState\":\"2\",\"ownerName\":\"%s\"}}", User)

	queryResults, err = getQueryResultForQueryString(stub, queryString)
	if err != nil {
		return shim.Error(err.Error())
	}
	res[1] = string(queryResults)

	queryString = fmt.Sprintf("{\"selector\":{\"docType\":\"bill\",\"phase\":\"1\",\"billState\":\"2\",\"invokeTarget\":\"%s\"}}", User)

	queryResults, err = getQueryResultForQueryString(stub, queryString)
	if err != nil {
		return shim.Error(err.Error())
	}
	res[2] = string(queryResults)

        return shim.Success([]byte(res[0] + "," + res[1] + "," + res[2]))
}

func (t *SimpleChaincode) promptdiscount(stub shim.ChaincodeStubInterface, args []string) pb.Response {
        var User, BillNumber, TargetName, UserAccount string
	var l, lval int
	var err error

	l = len(args)
        if l != 4 && l != 6 && l != 7 {
                return shim.Error("Incorrect number of arguments. Expecting 4 or 6 or 7")
        }
	if l == 4 {
		// lval = {1 : confirm, 0 : cancel}
		lval, err = strconv.Atoi(args[3])
		if err != nil {
			return shim.Error("Expecting integer value")
		}
		if lval != 0 && lval != 1 {
			return shim.Error("Bad confirm/cancel value")
		}
	}
	if l == 7 && strings.Compare("-1", args[6]) != 0 {
		return shim.Error("Bad args[6]: expected -1")
	}
	if l != 4 && strings.Compare(args[0], args[2]) == 0 {
			return shim.Error("User can't be as same as Target")
	}
	
	BillNumber = args[1]
	billAsBytes, err := stub.GetState("BillStruct" + BillNumber)
	if err != nil {
		return shim.Error("Failed to get bill:" + err.Error())
	} else if billAsBytes == nil {
		return shim.Error("Bill does not exist")
	}
	bill := bill{}
	err = json.Unmarshal(billAsBytes, &bill) //unmarshal it aka JSON.parse()
	if err != nil {
		return shim.Error(err.Error())
	}
	// Promise the bill is on stage 1
	BillStage := bill.Phase
	if (BillStage[0] != '1') {
		return shim.Error("Bad Bill Stage " + string(BillStage[0]) + ": Expected 1")
	}
	// And transfer is available
	TransferState := bill.ForbidTransfer
	if TransferState[0] == '1' {
		return shim.Error("The bill is forbidden to transfer")
	}
	TransferState = bill.TransferState
	if TransferState[0] == '9' {
		return shim.Error("The bill is forbidden to transfer")
	}
	// And circulation state is good
	CirculationState := bill.BillState
	if (l == 6 || l == 7) && CirculationState[0] != '0' {
	        return shim.Error("Bad circulation state " + CirculationState + " , which expected 0")
	}
	if l == 4 && CirculationState[0] != '2' {
	        return shim.Error("Bad circulation state " + CirculationState + " , which expected 2")
	}
	
	// User must be checked through information on the bill
	User = args[0]
	Userid, err := t.getuserid(stub, User)
        if err != nil {
                return shim.Error("Bad User name")
        }
        fmt.Printf("User = %s (%s)\n", User, string(Userid))
	TargetName = args[2]
	Userid, err = t.getuserid(stub, TargetName)
        if err != nil {
                return shim.Error("Bad target name")
        }
	if l == 6 || l == 7 || lval == 0 {
		UserAccount = bill.OwnerName
	} else if lval == 1 {
		UserAccount = bill.InvokeTarget
	}
	if strings.Compare(UserAccount, User) != 0 {
	        return shim.Error("User is " + User + " , which expected " + UserAccount)
	}
	
	if l == 6 || l == 7 {
		bill.BillState = "2"
		bill.InvokeTarget = TargetName
		//err = t.insertInvolve(stub, "hb", TargetName, BillNumber)
		//if err != nil {
		//		return shim.Error(err.Error())
		//}
		if l == 6 {
			return t.insertOp(stub, "OpS", []string{User, BillNumber, TargetName, "5", args[3] + "|" + args[4] + "|" + args[5]}, bill)
		} else {
			bill.TransferState = "1"
			return t.insertOp(stub, "OpS", []string{User, BillNumber, TargetName, "5", args[3] + "|" + args[4] + "|" + args[5] + "|-1"}, bill)
		}
	} else if l == 4 && lval == 1 {
		LastOperation := bill.InvokeOperation
		if err != nil {
			return shim.Error("Bad Bill number")
		}
		discountPrice, err := t.getDiscountPrice(stub, LastOperation)
		if err != nil {
			return shim.Error("Error on fetching operation information of promptdiscount: " + err.Error())
		}
		bill.BillState = "0"
		err = t.modifyAccount(stub, User, -discountPrice)
		if err != nil {
			return shim.Error(err.Error())
		}
		err = t.modifyAccount(stub, bill.OwnerName, discountPrice)
		if err != nil {
			return shim.Error(err.Error())
		}
		//err = t.removeInvolve(stub, "hb", User, BillNumber)
		//if err != nil {
		//		return shim.Error(err.Error())
		//}
		//LastOwner := bill.OwnerName
		//err = t.removeInvolve(stub, "g", string(LastOwner), BillNumber)
		//if err != nil {
		//		return shim.Error(err.Error())
		//}
		//err = t.insertInvolve(stub, "g", User, BillNumber)
		//if err != nil {
		//		return shim.Error(err.Error())
		//}
		bill, err = t.givewarrant(stub, bill)
		if err != nil {
			return shim.Error(err.Error())
		}
		bill.OwnerName = User
		if TransferState[0] == '1' {
			bill.TransferState = "9"
		}
		return t.insertOp(stub, "OpE", []string{User, BillNumber, "1", "5"}, bill)
	} else if l == 4 && lval == 0 {
		bill.BillState = "0"
		if TransferState[0] == '1' {
			bill.TransferState = "0"
			//LastTarget := bill.InvokeTarget
			//err = t.removeInvolve(stub, "hb", string(LastTarget), BillNumber)
			//if err != nil {
			//		return shim.Error(err.Error())
			//}
		}
		return t.insertOp(stub, "OpE", []string{User, BillNumber, "0", "5"}, bill)
	}
	return shim.Error("Bad arguments combination")
}

func (t *SimpleChaincode) querypayment(stub shim.ChaincodeStubInterface, args []string) pb.Response {
        var User string
	var res = [3]string{"","",""}
        var err error

        if len(args) != 1 {
                return shim.Error("Incorrect number of arguments. Expecting name of the person to query")
        }
		
        User = args[0]
		
	queryString := fmt.Sprintf("{\"selector\":{\"docType\":\"bill\",\"phase\":\"2\",\"finishState\":\"0\",\"ownerName\":\"%s\"}}", User)

	queryResults, err := getQueryResultForQueryString(stub, queryString)
	if err != nil {
		return shim.Error(err.Error())
	}
	res[0] = string(queryResults)

	queryString = fmt.Sprintf("{\"selector\":{\"docType\":\"bill\",\"phase\":\"2\",\"finishState\":\"1\",\"ownerName\":\"%s\"}}", User)

	queryResults, err = getQueryResultForQueryString(stub, queryString)
	if err != nil {
		return shim.Error(err.Error())
	}
	res[1] = string(queryResults)

	queryString = fmt.Sprintf("{\"selector\":{\"docType\":\"bill\",\"phase\":\"2\",\"$or\":[{\"finishState\":\"1\",\"payerName\":\"%s\"},{\"finishState\":\"3\",\"invokeTarget\":\"%s\"}]}}", User, User)

	queryResults, err = getQueryResultForQueryString(stub, queryString)
	if err != nil {
		return shim.Error(err.Error())
	}
	res[2] = string(queryResults)

        return shim.Success([]byte(res[0] + "," + res[1] + "," + res[2]))
}

func (t *SimpleChaincode) promptpayment(stub shim.ChaincodeStubInterface, args []string) pb.Response {
        var User, BillNumber, UserAccount string
	var l, lval int
	var err error

	l = len(args)
        if l < 2 || l > 3 {
                return shim.Error("Incorrect number of arguments. Expecting 2 or 3")
        }
	if l == 3 {
		// lval = {1 : confirm, 0 : cancel, -1 : refuse}
		lval, err = strconv.Atoi(args[2])
		if err != nil {
				return shim.Error("Expecting integer value")
		}
		if lval != 0 && lval != 1 && lval != -1 {
				return shim.Error("Bad confirm/cancel value")
		}
	}
	
	BillNumber = args[1]
	billAsBytes, err := stub.GetState("BillStruct" + BillNumber)
	if err != nil {
		return shim.Error("Failed to get bill:" + err.Error())
	} else if billAsBytes == nil {
		return shim.Error("Bill does not exist")
	}
	bill := bill{}
	err = json.Unmarshal(billAsBytes, &bill) //unmarshal it aka JSON.parse()
	if err != nil {
		return shim.Error(err.Error())
	}

	// Promise the bill is on stage 2
	BillStage := bill.Phase
	if (BillStage[0] != '2') {
			return shim.Error("Bad Bill Stage " + string(BillStage[0]) + ": Expected 2")
	}
	// And finish state is good
	FinishState := bill.FinishState
	if l == 2 && FinishState[0] != '0' {
	        return shim.Error("Bad finish state " + FinishState + " , which expected 0")
	}
	if l == 3 && FinishState[0] != '1' && FinishState[0] != '3' {
	        return shim.Error("Bad finish state " + FinishState + " , which expected 1 or 3")
	}
	if l == 3 && FinishState[0] == '3' && lval == 0 {
	        return shim.Error("Bad finish state " + FinishState + " , which expected 1 when operation type is cancel")
	}
	
	// User must be checked through information on the bill
	User = args[0]
	Userid, err := t.getuserid(stub, User)
        if err != nil {
                return shim.Error("Bad User name")
        }
        fmt.Printf("User = %s (%s)\n", User, string(Userid))
	if l == 2 || (l == 3 && lval == 0) {
		UserAccount = bill.OwnerName
	} else if l == 3 && FinishState[0] == '1' {
		UserAccount = bill.PayerName
	} else if l == 3 && FinishState[0] == '3' {
		UserAccount = bill.InvokeTarget
	}
	if strings.Compare(UserAccount, User) != 0 {
	        return shim.Error("User is " + User + " , which expected " + UserAccount)
	}
		
	if l == 2 {
		bill.FinishState = "1"
		return t.insertOp(stub, "OpS", []string{User, BillNumber, bill.PayerName, "8"}, bill)
	} else if l == 3 && lval == 1 {
		err = t.modifyAccount(stub, UserAccount, -bill.Price)
		if err != nil {
			return shim.Error(err.Error())
		}
		err = t.modifyAccount(stub, bill.OwnerName, bill.Price)
		if err != nil {
			return shim.Error(err.Error())
		}
		bill.FinishState = "9"
		return t.insertOp(stub, "OpE", []string{User, BillNumber, "1", "8"}, bill)
	} else if l == 3 && lval == 0 {
		bill.FinishState = "0"
		return t.insertOp(stub, "OpE", []string{User, BillNumber, "0", "8"}, bill)
	} else if l == 3 && lval == -1 {
		bill.FinishState = "2"
		return t.insertOp(stub, "OpE", []string{User, BillNumber, "-1", "8"}, bill)
	}
	return shim.Error("Bad arguments combination")
}

func (t *SimpleChaincode) queryrecourse(stub shim.ChaincodeStubInterface, args []string) pb.Response {
        var User string
	var res = [2]string{"",""}
        var err error

        if len(args) != 1 {
                return shim.Error("Incorrect number of arguments. Expecting name of the person to query")
        }
		
        User = args[0]
		
	queryString := fmt.Sprintf("{\"selector\":{\"docType\":\"bill\",\"phase\":\"2\",\"finishState\":\"2\",\"ownerName\":\"%s\"}}", User)

	queryResults, err := getQueryResultForQueryString(stub, queryString)
	if err != nil {
		return shim.Error(err.Error())
	}
	res[0] = string(queryResults)

	queryString = fmt.Sprintf("{\"selector\":{\"docType\":\"bill\",\"phase\":\"2\",\"finishState\":\"3\",\"ownerName\":\"%s\"}}", User)

	queryResults, err = getQueryResultForQueryString(stub, queryString)
	if err != nil {
		return shim.Error(err.Error())
	}
	res[1] = string(queryResults)

        return shim.Success([]byte(res[0] + "," + res[1]))
}

func (t *SimpleChaincode) promptrecourse(stub shim.ChaincodeStubInterface, args []string) pb.Response {
        var User, BillNumber, TargetName, UserAccount string
	var l, lval int
	var err error

	l = len(args)
        if l < 3 || l > 4 {
                return shim.Error("Incorrect number of arguments. Expecting 3 or 4")
        }
	if l == 4 {
		// lval = {0 : cancel}
		lval, err = strconv.Atoi(args[3])
		if err != nil {
				return shim.Error("Expecting integer value")
		}
		if lval != 0 {
				return shim.Error("Bad confirm/cancel value")
		}
	}
	
	BillNumber = args[1]
	billAsBytes, err := stub.GetState("BillStruct" + BillNumber)
	if err != nil {
		return shim.Error("Failed to get bill:" + err.Error())
	} else if billAsBytes == nil {
		return shim.Error("Bill does not exist")
	}
	bill := bill{}
	err = json.Unmarshal(billAsBytes, &bill) //unmarshal it aka JSON.parse()
	if err != nil {
		return shim.Error(err.Error())
	}

	// Promise the bill is on stage 2
	BillStage := bill.Phase
	if (BillStage[0] != '2') {
			return shim.Error("Bad Bill Stage " + string(BillStage[0]) + ": Expected 2")
	}
	// And finish state is good
	FinishState := bill.FinishState
	if l == 3 && FinishState[0] != '2' {
	        return shim.Error("Bad finish state " + FinishState + " , which expected 2")
	}
	if l == 4 && FinishState[0] != '3' {
	        return shim.Error("Bad finish state " + FinishState + " , which expected 3")
	}
	
	// User must be checked through information on the bill
	User = args[0]
	Userid, err := t.getuserid(stub, User)
        if err != nil {
                return shim.Error("Bad User name")
        }
        fmt.Printf("User = %s (%s)\n", User, string(Userid))
	UserAccount = bill.OwnerName
	if strings.Compare(UserAccount, User) != 0 {
	        return shim.Error("User is " + User + " , which expected " + UserAccount)
	}

	TargetName = args[2]
	Promptstate, err := t.getbillstatewarrant(stub, TargetName, bill)
        if err != nil {
                return shim.Error("Bad Bill state of warrant")
        }
	if Promptstate < 0 {
	        return shim.Error("Target is not a warrantor")
	}

	if l == 3 {
		bill.FinishState = "3"
		bill.InvokeTarget = TargetName
		return t.insertOp(stub, "OpS", []string{User, BillNumber, bill.InvokeTarget, "9"}, bill)
	} else if l == 4 {
		bill.FinishState = "2"
		return t.insertOp(stub, "OpE", []string{User, BillNumber, "0", "9"}, bill)
	}
	return shim.Error("Bad arguments combination")
}

func (t *SimpleChaincode) register(stub shim.ChaincodeStubInterface, args []string) pb.Response {
		// Register an user account with some money.
        var A string    // Entities
        var Aval int // Asset holdings
        var err error

        if len(args) != 2 {
                return shim.Error("Incorrect number of arguments. Expecting 2")
        }

        // Initialize the chaincode
        A = args[0]
        Aval, err = strconv.Atoi(args[1])
        if err != nil {
                return shim.Error("Expecting integer value for asset holding")
        }
        fmt.Printf("%s val = %d\n", A, Aval)

	// check if user already exists
	UserBytes, err := stub.GetState("Use0" + A)
        if err == nil {
                if len(UserBytes) > 0 {
			return shim.Error("That user already exists.")
		}
        }
	// UserCount ++
	UserCountBytes, err := stub.GetState("GlobUsec")
        if err != nil {
                jsonResp := "{\"Error\":\"Failed to get Global UserCount " + "\"}"
                return shim.Error(jsonResp)
        }
	UserCount, _ := strconv.Atoi(string(UserCountBytes))
	UserCount = UserCount + 1
	UserCountString := strconv.Itoa(UserCount)
	err = stub.PutState("GlobUsec", []byte(UserCountString))
        if err != nil {
                return shim.Error(err.Error())
        }
        // Write the state to the ledger
        err = stub.PutState("Use0" + A, []byte(UserCountString))
        if err != nil {
                return shim.Error(err.Error())
        }
        //err = stub.PutState("Use1" + A, []byte("0"))
        //if err != nil {
        //        return shim.Error(err.Error())
        //}
        err = stub.PutState("Use2" + A, []byte(strconv.Itoa(Aval)))
        if err != nil {
                return shim.Error(err.Error())
        }

        return shim.Success(nil)
}

// Deletes an entity from state
func (t *SimpleChaincode) delete(stub shim.ChaincodeStubInterface, args []string) pb.Response {
        if len(args) != 1 {
                return shim.Error("Incorrect number of arguments. Expecting 1")
        }

        A := args[0]

	fmt.Printf("deleting key %s\n", A)

        // Delete the key from the state in ledger
        err := stub.DelState(A)
        if err != nil {
                return shim.Error("Failed to delete state")
        }

        return shim.Success(nil)
}

// Query callback representing the query of a chaincode
func (t *SimpleChaincode) query(stub shim.ChaincodeStubInterface, args []string) pb.Response {
        var A string // Entities
        var err error

        if len(args) != 1 {
                return shim.Error("Incorrect number of arguments. Expecting name of the person to query")
        }

        A = args[0]

        // Get the state from the ledger

	var key string

	if strings.HasPrefix(A, "BillStruct") {
		key = A
	}else {
		key = "Use0" + A
	}

	fmt.Printf("querying with key %s\n", key)

        Avalbytes, err := stub.GetState(key)
        if err != nil {
                jsonResp := "{\"Error\":\"Failed to get state for " + A + "\"}"
                return shim.Error(jsonResp)
        }

        if Avalbytes == nil {
                jsonResp := "{\"Error\":\"Nil amount for " + A + "\"}"
                return shim.Error(jsonResp)
        }

        jsonResp := "{\"Name\":\"" + A + "\",\"Amount\":\"" + string(Avalbytes) + "\"}"
        fmt.Printf("Query Response:%s\n", jsonResp)
        return shim.Success(Avalbytes)
}

func main() {
        err := shim.Start(new(SimpleChaincode))
        if err != nil {
                fmt.Printf("Error starting Simple chaincode: %s", err)
        }
}
