--------------------------------------------------
-- 로그인 테스트용 계정 (프론트 데모 계정과 동일)
--   이메일: seoyeon.kim@email.com / 휴대폰: 01012345678 / 비밀번호: Yeobaek!2026
--
-- 비밀번호는 pgcrypto 의 crypt(..., gen_salt('bf')) 로 BCrypt 해시를 만들어 저장한다.
-- 스프링의 BCryptPasswordEncoder 와 같은 형식($2a$)이라 그대로 비교된다.
-- 여러 번 실행해도 계정이 중복으로 생기지 않는다.
--------------------------------------------------
create extension if not exists pgcrypto;

insert into tbl_member (member_email, member_phone, member_password, member_name, member_alias, member_bio)
select 'seoyeon.kim@email.com', '01012345678', crypt('Yeobaek!2026', gen_salt('bf', 10)),
       '김서연', '파란여백', '서두르지 않고 오래 기억하는 여행'
where not exists (select 1 from tbl_member where member_email = 'seoyeon.kim@email.com');

insert into tbl_auth (id, member_role, member_type)
select id, 'MEMBER', 'YEOBEAK'
from tbl_member
where member_email = 'seoyeon.kim@email.com'
  and not exists (select 1 from tbl_auth a where a.id = tbl_member.id);
