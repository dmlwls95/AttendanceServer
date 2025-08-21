<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>주간 근태 현황</title>
<style>
body {
	font-family: Arial, Helvetica, sans-serif;
	margin: 24px;
}

h2 {
	margin-bottom: 8px;
}

table {
	width: 100%;
	border-collapse: collapse;
	margin-top: 16px;
}

th, td {
	border: 1px solid #ddd;
	padding: 8px;
	text-align: center;
}

th {
	background-color: #f4f4f4;
}
</style>
</head>
<body>
	<h2>주간 근태 현황</h2>
	<p>사용자: ${userid}</p>
	<p>기간: ${start} ~ ${end}</p>

	<c:choose>
		<c:when test="${empty weeklySummary}">
			<p>해당 기간의 데이터가 없습니다.</p>
		</c:when>
		<c:otherwise>
			<table>
				<thead>
					<tr>
						<th>근무일</th>
						<th>요일</th>
						<th>근무시간</th>
						<th>연장시간</th>
						<th>총 근무시간</th>
						<th>상태</th>
						<th>출근시각</th>
					</tr>
				</thead>

				<tbody>
					<c:forEach var="work" items="${weeklySummary}">
						<tr>
							<td><fmt:formatDate value="${work.workdate}"
									pattern="yyyy-MM-dd" /></td>
							<td>${work.dayofweek}</td>
							<td>${work.workinghours}</td>
							<td>${work.extendhours}</td>
							<td>${work.totalhours}</td>
							<td>${work.workstatus}</td>
							<td><c:choose>
									<c:when test="${not empty work.arrivaltime}">
										<fmt:formatDate value="${work.arrivaltime}" pattern="HH:mm" />
									</c:when>
									<c:otherwise>-</c:otherwise>
								</c:choose></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:otherwise>
	</c:choose>

</body>
</html>